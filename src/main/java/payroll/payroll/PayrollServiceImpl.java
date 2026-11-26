package payroll.payroll;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.Predicate;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import payroll.common.dto.ResultDTO;
import payroll.common.excel.ExcelService;
import payroll.entity.*;
import payroll.holiday.HolidayService;
import payroll.payroll.dto.*;
import payroll.repository.*;
import tools.jackson.databind.JsonNode;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PayrollServiceImpl implements PayrollService {

    private final PayrollHistoryRepository payrollHistoryRepository;
    private final EmployeeRepository employeeRepository;
    private final WorkHistoryRepository workHistoryRepository;
    private final LeaveRecordRepository leaveRecordRepository;
    private final AttendanceRecordRepository attendanceRecordRepository;
    private final InsuranceRateHistoryRepository insuranceRateHistoryRepository;
    private final LeaveUsageRepository leaveUsageRepository;
    private final DepartmentRepository departmentRepository;
    private final HolidayService holidayService;
    private final ExcelService excelService;

    @Override
    public Page<PayrollResponseDTO> findPayroll(PayrollSearchDTO searchDTO,
                                                Pageable pageable) {
        Specification<PayrollHistory> spec = createSpec(searchDTO);

        return payrollHistoryRepository.findAll(spec, pageable)
                .map(payrollHistory -> {
                    Employee emp = employeeRepository.findById(payrollHistory.getEmpId())
                            .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 사원입니다"));

                    return new PayrollResponseDTO(payrollHistory, emp);
                });
    }

    @Override
    public PayrollResponseDTO findPayrollById(Long id) {
        PayrollHistory payroll = payrollHistoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 급여 정보입니다"));

        Employee emp = employeeRepository.findById(payroll.getEmpId())
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 사원입니다"));

        return new PayrollResponseDTO(payroll, emp);
    }

    @Override
    public PayslipPDFResponseDTO findPayslipDataById(Long id) {
        PayrollHistory payroll = payrollHistoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 급여 정보입니다"));

        Employee emp = employeeRepository.findById(payroll.getEmpId())
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 사원입니다"));

        String deptName = departmentRepository.findNameById(emp.getDeptId());

        WorkHistory workHistory = workHistoryRepository.findByEmpIdAndYearAndMonth(emp.getId(), payroll.getYear(), payroll.getMonth())
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 근무 기록입니다"));

        int year = payroll.getYear();
        int month = payroll.getMonth();

        InsuranceRateHistory insuranceRate = insuranceRateHistoryRepository.findInsuranceRate(LocalDate.of(year, month, 1))
                .orElseThrow(() -> new IllegalArgumentException("다시 시도해주세요"));


        return new PayslipPDFResponseDTO(new PayrollResponseDTO(payroll, emp), emp, workHistory, insuranceRate, deptName,
                calcPensionBase(emp, year, month), calcHealthBase(emp, year, month , emp.getJoiningDate()),
                isRetirement(emp.getRetirementDate(), year, month));
    }

    @Override
    @Transactional
    public void downloadPayrollExcel(HttpServletResponse response) throws IOException{
        List<String> headers = List.of(
                "사원코드", "연도", "월", "전월소급분", "미지급수당", "미지급수당 사유",
                "식대", "기타수당", "퇴직정산소득세", "소득세"
        );

        excelService.downloadExcel("급여대장", headers, response);
    }

    @Override
    @Transactional
    public ResultDTO savePayroll(PayrollRequestDTO requestDTO) {
        Employee emp = employeeRepository.findByCode(requestDTO.getEmpCode())
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 사원입니다"));
        if (payrollHistoryRepository.existsByEmpIdAndYearAndMonth(emp.getId(), requestDTO.getYear(), requestDTO.getMonth())) {
            throw new EntityExistsException("이미 존재하는 급여 기록입니다");
        }

        LocalDate resignationDate = emp.getRetirementDate();

        LocalDate startDate = LocalDate.of(
                requestDTO.getYear(), requestDTO.getMonth(), 1);

        if (resignationDate != null && startDate.isAfter(resignationDate)) {
            throw new IllegalArgumentException("퇴사 이후의 급여는 생성할 수 없습니다");
        }
        int hourlyWage = emp.getHourlyWage();
        // 지급항목
        PayrollHistoryPaymentDTO payment = calculatePayment(requestDTO, emp);
        // 급여합계
        int grossSalary = payment.grossSalary();
        // 공제항목
        PayrollHistoryDeductionDTO deduction = calculateDeduction(requestDTO, grossSalary, emp);
        // 공제합계
        int totalDeduction = deduction.totalDeduction();
        // 실수령액
        int netSalary = grossSalary - totalDeduction;

        PayrollHistory payroll = PayrollHistory.builder()
                .empId(emp.getId()).hourlyWage(hourlyWage)
                .basicSalary(payment.getBasicSalary()).weeklyHolidayPay(payment.getWeeklyHolidayPay())
                .retroactivePay(payment.getRetroactivePay()).deductionAmount(payment.getDeductionAmount())
                .monthlyLeavePay(payment.getMonthlyLeavePay()).dailyMealAllowance(payment.getDailyMealAllowance())
                .overtimePay(payment.getOverTimePay()).unpaidAllowance(payment.getUnpaidAllowance())
                .unpaidAllowanceReason(payment.getUnpaidAllowanceReason()).otherAllowance(payment.getOtherAllowance())
                .holidayWorkPay(payment.getHolidayWorkPay())
                .nationalPension(deduction.getNationalPension()).healthInsurance(deduction.getHealthInsurance())
                .longTermCareInsurance(deduction.getLongTermCareInsurance()).employmentInsurance(deduction.getEmploymentInsurance())
                .healthInsuranceAdjustment(deduction.getHealthInsuranceAdjustment()).longTermCareAdjustment(deduction.getLongTermCareAdjustment())
                .retirementIncomeTax(deduction.getRetirementIncomeTax()).retirementLocalIncomeTax(deduction.getRetirementLocalIncomeTax())
                .incomeTax(deduction.getIncomeTax()).localIncomeTax(deduction.getLocalIncomeTax())
                .grossSalary(grossSalary).totalDeduction(totalDeduction).netSalary(netSalary)
                .year(requestDTO.getYear()).month(requestDTO.getMonth())
                .build();

        payrollHistoryRepository.save(payroll);

        return new ResultDTO("success", "급여 정보 저장 성공");
    }

    @Override
    @Transactional
    public ResultDTO updatePayroll(PayrollRequestDTO requestDTO, Long id) {
        Employee emp = employeeRepository.findByCode(requestDTO.getEmpCode())
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 사원입니다"));

        Long empId = emp.getId();
        // 지급항목
        PayrollHistoryPaymentDTO payment = calculatePayment(requestDTO, emp);
        // 급여합계
        int grossSalary = payment.grossSalary();
        // 공제항목
        PayrollHistoryDeductionDTO deduction = calculateDeduction(requestDTO, grossSalary, emp);
        // 공제합계
        int totalDeduction = deduction.totalDeduction();
        int netSalary = grossSalary - totalDeduction;

        PayrollHistory payrollHistory = payrollHistoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 급여 정보입니다"));

        payrollHistory.update(payment, deduction, grossSalary, totalDeduction, netSalary);

        return new ResultDTO("success", "급여 정보 수정 성공");
    }

    @Override
    @Transactional
    public ResultDTO deletePayroll(Long id) {
        PayrollHistory payroll = payrollHistoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 급여 정보입니다"));

        payroll.delete();

        return new ResultDTO("success", "급여 삭제 성공");
    }

    @Override
    @Transactional
    public ResultDTO deleteSelectedPayroll(List<Long> ids) {
        List<PayrollHistory> payrolls = payrollHistoryRepository.findAllById(ids);

        for (PayrollHistory payroll : payrolls) {
            payroll.delete();
        }

        return new ResultDTO("success", "급여 삭제 성공");
    }

    private boolean isRetirement(LocalDate retirementDate,int year, int month){
        if(retirementDate == null){
            return false;
        }
        int retirementYear = retirementDate.getYear();
        int retirementMonth = retirementDate.getMonthValue();

        return retirementYear == year && retirementMonth == month;
    }

    private Specification<PayrollHistory> createSpec(PayrollSearchDTO searchDTO) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.equal(root.get("deleteYn"), "N"));

            String keyword = searchDTO.getKeyword();
            if (StringUtils.hasText(keyword)) {
                Long empId = employeeRepository.findIdByNameOrCode(keyword)
                        .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 사원입니다"));
                predicates.add(cb.equal(root.get("empId"), empId));
            }

            if (searchDTO.getYear() != null) {
                predicates.add(cb.equal(root.get("year"), searchDTO.getYear()));
            }

            if (searchDTO.getMonth() != null) {
                predicates.add(cb.equal(root.get("month"), searchDTO.getMonth()));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    // 지급항목 계산
    private PayrollHistoryPaymentDTO calculatePayment(PayrollRequestDTO requestDTO, Employee emp) {
        Long empId = emp.getId();

        int year = requestDTO.getYear();
        int month = requestDTO.getMonth();
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());

        // 특정 사원의 특정 달 근로 정보 (총 근로시간, 총 근로일, 주차일수, 주차시간 등)
        WorkHistory workHistory = workHistoryRepository.findByEmpIdAndYearAndMonth(empId, year, month)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 근무기록입니다"));

        // 휴일 근로 수당 계산을 위한 특정 사원, 특정 달의 일단위 근로 정보
        List<AttendanceRecord> attendance = attendanceRecordRepository.findByEmpIdPeriod(empId, start, end);

        int hourlyWage = emp.getHourlyWage();
        int contractDailyMinutes = emp.getContractDailyWorkMinutes();
        int totalWorkMinutes = workHistory.getTotalWorkMinutes();
        int weeklyHolidayDays = workHistory.getWeeklyHolidayDays();

        LocalDate joiningDate = emp.getJoiningDate();

        int holidayMinutes = 0;

        JsonNode holidays = holidayService.getHolidays(year, month)
                .path("response")
                .path("body")
                .path("items")
                .path("item");

        // 평일 공휴일 존재할 시 일당 추가
        for (JsonNode holiday : holidays) {
            LocalDate holidayDate = LocalDate.parse(
                    holiday.path("locdate").asString(),
                    DateTimeFormatter.BASIC_ISO_DATE
            );
            if (holidayDate.getDayOfWeek() != DayOfWeek.SATURDAY &&
                    holidayDate.getDayOfWeek() != DayOfWeek.SUNDAY &&
                    !holidayDate.isBefore(joiningDate) &&
                    (emp.getRetirementDate() == null || !holidayDate.isAfter(emp.getRetirementDate()))) {
                holidayMinutes += emp.getContractDailyWorkMinutes();
            }
        }

        List<LeaveDate> leaveDates = leaveUsageRepository.findLeave(empId, start, end).stream()
                .filter(leave -> leave.getDeleteYn().equals("N"))
                .flatMap(leave -> leave.getStartDate()
                        .datesUntil(leave.getEndDate().plusDays(1))
                        .map(date -> new LeaveDate(date, leave.getLeaveType())))
                .filter(leave -> !holidayService.isHoliday(leave.date))
                .filter(leave -> leave.date.getDayOfWeek() != DayOfWeek.SATURDAY)
                .filter(leave -> leave.date.getDayOfWeek() != DayOfWeek.SUNDAY)
                .toList();

        int leaveMinutes = leaveDates.stream()
                .mapToInt(leave -> {
                    if (leave.leaveType.equals("휴가")) {
                        return emp.getContractDailyWorkMinutes();
                    } else if (leave.leaveType.equals("반차")) {
                        return 300;
                    } else {
                        return 120;
                    }
                }).sum();

        // 기본급
        int basicSalary = ((totalWorkMinutes + leaveMinutes + holidayMinutes) * hourlyWage) / 60;

        // 주차수당
        int weeklyHolidayPay = (weeklyHolidayDays * hourlyWage * contractDailyMinutes) / 60;
        // 전월소급분
        int retroactivePay = requestDTO.getRetroactivePay();

        // 월차수당
        // 입사 1년차 미만 시 월 개근마다 월차 1개 획득, 입사 1년 전까지 유지되다가 입사 1년과 동시에 사라짐
        // 입사 1년 이상 시 매년 초 연차 발생 그 해 말 연차 미사용 삭제
        int monthlyLeavePay = 0;

        LocalDate oneYearDate = joiningDate.plusYears(1);

        // 급여 명세서 날짜 사이에 입사 1년이 포함될 시 그 명세서에 월차수당 계산
        if (!oneYearDate.isBefore(start)
                && !oneYearDate.isAfter(end)) {
            // 입사연도부터 1주년 연도까지 잔여 월차 합산
            int remainingLeave = leaveRecordRepository.sumRemainingLeave(empId, joiningDate.getYear(), oneYearDate.getYear());

            monthlyLeavePay = remainingLeave * hourlyWage * contractDailyMinutes / 60;
        } else if (month == 1 && year > joiningDate.getYear() + 1) {
            int remainingLeave = leaveRecordRepository.findRemainingLeaveByEmpIdAndYear(empId, year - 1);
            monthlyLeavePay = remainingLeave * hourlyWage * contractDailyMinutes / 60;
        }

        // 식대
        int dailyMealAllowance = requestDTO.getDailyMealAllowance() * workHistory.getWorkDays();
        // 연장근로수당
        int overtimePay = (int) (workHistory.getOvertimeWorkMinutes() * hourlyWage * 1.5 / 60);
        // 미지급수당
        int unpaidAllowance = requestDTO.getUnpaidAllowance();
        // 미지급수당 사유
        validUnpaidAllowance(unpaidAllowance, requestDTO.getUnpaidAllowanceReason());
        String unpaidAllowanceReason = requestDTO.getUnpaidAllowanceReason();
        // 기타수당
        int otherAllowance = requestDTO.getOtherAllowance();
        // 휴일근로수당
        int holidayWorkPay = attendance.stream()
                .mapToInt(wh -> {
                    int minutes = wh.getHolidayWorkMinutes();

                    int normalMinutes = Math.min(minutes, emp.getContractDailyWorkMinutes());
                    int overtimeWorkMinutes = Math.max(minutes - emp.getContractDailyWorkMinutes(), 0);

                    return (int) (normalMinutes * hourlyWage * 1.5 / 60) +
                            (overtimeWorkMinutes * hourlyWage * 2 / 60);
                }).sum();

        return PayrollHistoryPaymentDTO.builder()
                .basicSalary(basicSalary)
                .weeklyHolidayPay(weeklyHolidayPay)
                .monthlyLeavePay(monthlyLeavePay)
                .overTimePay(overtimePay)
                .holidayWorkPay(holidayWorkPay)
                .dailyMealAllowance(dailyMealAllowance)
                .retroactivePay(retroactivePay)
                .unpaidAllowance(unpaidAllowance)
                .unpaidAllowanceReason(unpaidAllowanceReason)
                .otherAllowance(otherAllowance)
                .build();
    }

    // 미지급수당 사유 검증
    private void validUnpaidAllowance(int unpaidAllowance, String reason) {
        if (unpaidAllowance == 0 && StringUtils.hasText(reason)) {
            throw new IllegalArgumentException("미지급 수당이 없으면 사유 입력이 불가능합니다");
        }
        if (unpaidAllowance > 0 && !StringUtils.hasText(reason)) {
            throw new IllegalArgumentException("미지급 수당 사유를 입력해주세요");
        }
    }

    // 공제항목 계산
    private PayrollHistoryDeductionDTO calculateDeduction(PayrollRequestDTO requestDTO, int grossSalary, Employee emp) {
        LocalDate date = LocalDate.of(requestDTO.getYear(), requestDTO.getMonth(), 15);

        InsuranceRateHistory insuranceRate = insuranceRateHistoryRepository.findInsuranceRate(date)
                .orElseThrow(() -> new EntityNotFoundException("해당 조건의 공제율이 없습니다"));

        // 국민연금 공제 비율
        BigDecimal nationalPensionRate = insuranceRate.getNationalPensionRate();
        // 건강보험 공제 비율
        BigDecimal healthInsuranceRate = insuranceRate.getHealthInsuranceRate();
        // 노인장기요양 공제 비율
        BigDecimal longTermCareInsuranceRate = insuranceRate.getLongTermCareInsuranceRate();
        // 고용보험 공제 비율
        BigDecimal employmentInsuranceRate = insuranceRate.getEmploymentInsuranceRate();
        // 주민세 공제 비율
        BigDecimal localIncomeTaxRate = insuranceRate.getLocalIncomeTaxRate();

        Long empId = emp.getId();
        LocalDate joiningDate = emp.getJoiningDate();

        // 출력될 급여명세서 해
        int year = requestDTO.getYear();
        int month = requestDTO.getMonth();

        // 기준소득월액
        int nationalPensionBase = calcPensionBase(emp, year, month);
        if(nationalPensionBase == 0){
            nationalPensionBase = emp.getInitialMonthlyIncome();
        }
        // 건강보험 계산
        int healthInsuranceBase = calcHealthBase(emp, year, month, joiningDate);
        if(healthInsuranceBase == 0){
            healthInsuranceBase = emp.getInitialMonthlyIncome();
        }
        // 건강보험정산
        int healthAdjustment;

        if (month == 4) {
            healthAdjustment = calcHealthAdjustment(empId, year, month, joiningDate, emp);
        } else {
            healthAdjustment = 0;
        }

        // 국민연금 계산
        int nationalPension = floorUnderTen(nationalPensionBase, nationalPensionRate);
        // 건강보험 계산 (계약직평균급여 * 건강보험 공제 비율)
        int healthInsurance = floorUnderTen(healthInsuranceBase, healthInsuranceRate);
        // 노인장기요양 계산 (건강보험 공제금 * 노인장기요양 공제 비율)
        int longTermCareInsurance = floorUnderTen(healthInsurance, longTermCareInsuranceRate);
        // 고용보험 계산 (급여합계 * 고용보험 공제 비율)
        int employmentInsurance = floorUnderTen(grossSalary, employmentInsuranceRate);
        // 건강보험정산
        int healthInsuranceAdjustment = healthAdjustment;
        // 요양보험정산 계산 (건강보험정산 * 노양장기요양 공제 비율)
        int longTermCareAdjustment = floorUnderTen(healthInsuranceAdjustment, longTermCareInsuranceRate);
        // 퇴직정산소득세
        int retirementIncomeTax = requestDTO.getRetirementIncomeTax();
        // 퇴직정산주민세
        int retirementLocalIncomeTax = floorUnderTen(retirementIncomeTax, localIncomeTaxRate);
        // 소득세
        int incomeTax = requestDTO.getIncomeTax();
        // 주민세 (소득세 * 주민세 공제 비율)
        int localIncomeTax = floorUnderTen(incomeTax, localIncomeTaxRate);

        return PayrollHistoryDeductionDTO.builder()
                .nationalPension(nationalPension)
                .healthInsurance(healthInsurance)
                .longTermCareInsurance(longTermCareInsurance)
                .employmentInsurance(employmentInsurance)
                .healthInsuranceAdjustment(healthInsuranceAdjustment)
                .longTermCareAdjustment(longTermCareAdjustment)
                .retirementIncomeTax(retirementIncomeTax)
                .retirementLocalIncomeTax(retirementLocalIncomeTax)
                .incomeTax(incomeTax)
                .localIncomeTax(localIncomeTax)
                .build();
    }

    // 공제액 계산 10원 미만 삭제
    private int floorUnderTen(int amount, BigDecimal rate) {
        rate = rate.divide(BigDecimal.valueOf(100));
        return BigDecimal.valueOf(amount)
                .multiply(rate)
                .divideToIntegralValue(BigDecimal.TEN)
                .intValue() * 10;
    }

    private int calcPensionBase(Employee emp, int year, int month) {
        int pensionYear = month >= 7 ? year - 1 : year - 2;

        // 소득 총액
        int totalBase = payrollHistoryRepository.sumYearGrossSalary(emp.getId(), pensionYear)
                .orElse(0);
        if (totalBase == 0) {
            return 0;
        }

        // 기준소득월액(국민연금) : 소득총액 / 종사기간 일수 x 30
        return (int) (totalBase * 30L / employmentDays(pensionYear, emp) / 1000 * 1000);
    }

    // 근무일수
    private int employmentDays(int pensionYear, Employee emp) {
        LocalDate startDate = LocalDate.of(pensionYear, 1, 1);
        LocalDate endDate = LocalDate.of(pensionYear, 12, 31);
        LocalDate joiningDate = emp.getJoiningDate();

        if (joiningDate.isAfter(startDate)) {
            startDate = joiningDate;
        }

        return (int) ChronoUnit.DAYS.between(startDate, endDate) + 1;
    }

    // 건강보험 계산 함수
    private int calcHealthBase(Employee emp, int year, int month, LocalDate joiningDate) {
        Long empId = emp.getId();
        int healthYear = month >= 4 ? year - 1 : year - 2;

        if (joiningDate.isAfter(LocalDate.of(healthYear, 12, 1))) {
            return 0;
        }

        int totalSalary = payrollHistoryRepository.sumYearGrossSalary(empId, healthYear)
                .orElse(0);
        if (totalSalary == 0) {
            return 0;
        }

        LocalDate start = LocalDate.of(healthYear, 1, 1);
        LocalDate end = LocalDate.of(healthYear, 12, 31);

        LocalDate workStart = joiningDate.isAfter(start)
                ? joiningDate : start;

        LocalDate retirementDate = emp.getRetirementDate();

        LocalDate workEnd = retirementDate != null && retirementDate.isBefore(end)
                ? retirementDate : end;

        if (workStart.isAfter(workEnd)) {
            throw new IllegalArgumentException("해당 연도의 근무 기록이 없습니다.");
        }

        int workMonths = workEnd.getMonthValue() - workStart.getMonthValue() + 1;

        return totalSalary / workMonths;
}

    private int calcHealthAdjustment(Long empId, int year, int month, LocalDate joiningDate, Employee emp) {
        int baseYear = month >= 4 ? year - 1 : year - 2;
        // 보험금 부과 시작일
        LocalDate insuranceStart = joiningDate.getDayOfMonth() == 1 ? joiningDate : joiningDate.plusMonths(1).withDayOfMonth(1);

        LocalDate startYear = LocalDate.of(baseYear, 1, 1);
        LocalDate endYear = LocalDate.of(baseYear, 12, 31);

        if (insuranceStart.isBefore(startYear)) {
            insuranceStart = startYear;
        } else if (insuranceStart.isAfter(endYear)) {
            return 0;
        }

        LocalDate retirementDate = emp.getRetirementDate();

        LocalDate insuranceEnd = retirementDate != null && !retirementDate.isAfter(endYear)
                ? retirementDate : endYear;

        if (insuranceStart.isAfter(insuranceEnd)) {
            return 0;
        }

        int insuranceMonths = insuranceEnd.getMonthValue() - insuranceStart.getMonthValue() + 1;

        int previousSumHealthInsurance = payrollHistoryRepository.sumYearHealthInsurance(empId, baseYear)
                .orElseThrow(() -> new EntityNotFoundException("건강보험 총액을 구할 수 없습니다"));

        int healthAdjustmentBase = calcHealthBase(emp, year, month, joiningDate);

        BigDecimal previousRate = insuranceRateHistoryRepository
                .findInsuranceRate(LocalDate.of(baseYear, 12, 15))
                .orElseThrow(() -> new EntityNotFoundException("전년도 공제율이 없습니다"))
                .getHealthInsuranceRate();

        int newSumHealthInsurance =
                floorUnderTen(healthAdjustmentBase, previousRate) * insuranceMonths;

        return newSumHealthInsurance - previousSumHealthInsurance;
    }

    private record LeaveDate(LocalDate date, String leaveType) {}

}
