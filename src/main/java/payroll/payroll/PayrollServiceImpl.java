package payroll.payroll;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import payroll.common.dto.ResultDTO;
import payroll.entity.*;
import payroll.payroll.dto.*;
import payroll.repository.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PayrollServiceImpl implements PayrollService{

    private final PayrollHistoryRepository payrollHistoryRepository;
    private final EmployeeRepository employeeRepository;
    private final WorkHistoryRepository workHistoryRepository;
    private final LeaveRecordRepository leaveRecordRepository;
    private final AttendanceRecordRepository attendanceRecordRepository;
    private final DepartmentRepository departmentRepository;
    private final InsuranceRateHistoryRepository insuranceRateHistoryRepository;

    @Override
    public Page<PayrollResponseDTO> findPayroll(PayrollSearchDTO searchDTO,
                                                Pageable pageable){
        Specification<PayrollHistory> spec = createSpec(searchDTO);

        return payrollHistoryRepository.findAll(spec, pageable)
                .map(payrollHistory -> {
                    Employee emp = employeeRepository.findById(payrollHistory.getEmpId())
                            .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 사원입니다."));
                    String empCode = emp.getCode();
                    String empName = emp.getName();

                    Long deptId = emp.getDeptId();
                    String deptName = departmentRepository.findNameById(deptId);

                    return new PayrollResponseDTO(payrollHistory, empCode, empName, deptName);
                });
    }

    @Override
    @Transactional
    public ResultDTO savePayroll(PayrollRequestDTO requestDTO){
        Employee emp = employeeRepository.findByCode(requestDTO.getEmpCode())
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 사원입니다."));
        if(payrollHistoryRepository.existsByEmpIdAndYearAndMonth(emp.getId(), requestDTO.getYear(), requestDTO.getMonth())){
            throw new EntityExistsException("해당 날짜의 사원 급여 기록이 이미 존재합니다.");
        }

        LocalDate resignationDate = emp.getRetirementDate();

        LocalDate endDate = LocalDate.of(
                requestDTO.getYear(), requestDTO.getMonth(), 14);

        LocalDate startDate = endDate.plusDays(1).minusMonths(1);

        if(resignationDate != null && startDate.isAfter(resignationDate)) {
            throw new IllegalArgumentException("퇴사 이후의 급여는 생성할 수 없습니다.");
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
                .empId(emp.getId()) .hourlyWage(hourlyWage)
                .basicSalary(payment.getBasicSalary()) .weeklyHolidayPay(payment.getWeeklyHolidayPay())
                .retroactivePay(payment.getRetroactivePay()) .deductionAmount(payment.getDeductionAmount())
                .monthlyHolidayPay(payment.getMonthlyLeavePay()) .mealAllowance(payment.getMealAllowance())
                .overtimePay(payment.getOverTimePay()) .unpaidAllowance(payment.getUnpaidAllowance())
                .unpaidAllowanceReason(payment.getUnpaidAllowanceReason()) .otherAllowance(payment.getOtherAllowance())
                .holidayWorkPay(payment.getHolidayWorkPay())
                .nationalPension(deduction.getNationalPension()) .healthInsurance(deduction.getHealthInsurance())
                .longTermCareInsurance(deduction.getLongTermCareInsurance()) .employmentInsurance(deduction.getEmploymentInsurance())
                .healthInsuranceAdjustment(deduction.getHealthInsuranceAdjustment()) .longTermCareAdjustment(deduction.getLongTermCareAdjustment())
                .retirementIncomeTax(deduction.getRetirementIncomeTax()) .retirementLocalIncomeTax(deduction.getRetirementLocalIncomeTax())
                .incomeTax(deduction.getIncomeTax()) .localIncomeTax(deduction.getLocalIncomeTax())
                .grossSalary(grossSalary) .totalDeductions(totalDeduction).netSalary(netSalary)
                .year(requestDTO.getYear()).month(requestDTO.getMonth())
                .build();

        payrollHistoryRepository.save(payroll);

        return new ResultDTO("success", "급여 정보 저장 성공");
    }

    @Override
    @Transactional
    public ResultDTO updatePayroll(PayrollRequestDTO requestDTO){
        Employee emp = employeeRepository.findByCode(requestDTO.getEmpCode())
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 사원입니다."));

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

        PayrollHistory payrollHistory = payrollHistoryRepository.findByEmpIdAndYearAndMonth(empId, requestDTO.getYear(), requestDTO.getMonth())
                        .orElseThrow(() -> new EntityNotFoundException("급여 기록을 찾을 수 없습니다."));

        payrollHistory.update(payment, deduction, grossSalary, totalDeduction, netSalary);

        return new ResultDTO("success", "급여 정보 수정 성공");
    }

    private Specification<PayrollHistory> createSpec(PayrollSearchDTO searchDTO){
        return(root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if(StringUtils.hasText(searchDTO.getEmpCode())){
                Long empId = employeeRepository.findIdByCode(searchDTO.getEmpCode())
                        .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 사원입니다."));
                predicates.add(cb.equal(root.get("empId"), empId));
            }

            if(searchDTO.getYear() != null){
                predicates.add(cb.equal(root.get("year"), searchDTO.getYear()));
            }

            if(searchDTO.getMonth() != null){
                predicates.add(cb.equal(root.get("month"), searchDTO.getMonth()));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    // 지급항목 계산
    private PayrollHistoryPaymentDTO calculatePayment(PayrollRequestDTO requestDTO, Employee emp){
        Long empId = emp.getId();

        int year = requestDTO.getYear();
        int month = requestDTO.getMonth();
        LocalDate end = LocalDate.of(year, month, 14);
        LocalDate start = end.plusDays(1).minusMonths(1);

        // 특정 사원의 특정 달 근로 정보 (총 근로시간, 총 근로일, 주차일수, 주차시간 등)
        WorkHistory workHistory = workHistoryRepository.findByEmpIdAndYearAndMonth(empId, year, month)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 근무기록입니다."));

        // 휴일 근로 수당 계산을 위한 특정 사원, 특정 달의 일단위 근로 정보
        List<AttendanceRecord> attendance = attendanceRecordRepository.findByEmpIdPeriod(empId, start, end);

        int hourlyWage = emp.getHourlyWage();
        int contractDailyHour = emp.getContractDailyWorkHour();
        int totalWorkHour = workHistory.getTotalWorkHour();
        int weeklyHolidayDays = workHistory.getWeeklyHolidayDays();

        LocalDate joiningDate = emp.getJoiningDate();

        // 기본급
        int basicSalary = totalWorkHour * hourlyWage;
        // 주차수당
        int weeklyHolidayPay = weeklyHolidayDays * hourlyWage * contractDailyHour;
        // 전월소급분
        int retroactivePay = requestDTO.getRetroactivePay();

        // 월차수당
        // 입사 1년차 미만 시 월 개근마다 월차 1개 획득, 입사 1년 전까지 유지되다가 입사 1년과 동시에 사라짐
        // 입사 1년 이상 시 매년 초 연차 발생 그 해 말 연차 미사용 삭제
        int monthlyLeavePay = 0;

        LocalDate oneYearDate = joiningDate.plusYears(1);

        LocalDate endDate = LocalDate.of(requestDTO.getYear(), requestDTO.getMonth(), 14);
        LocalDate startDate = endDate.plusDays(1).minusMonths(1);


        // 급여 명세서 날짜 사이에 입사 1년이 포함될 시 그 명세서에 월차수당 계산
        if (!oneYearDate.isBefore(startDate)
                && !oneYearDate.isAfter(endDate)) {

            // 입사연도부터 1주년 연도까지 잔여 월차 합산
            int remainingLeave = leaveRecordRepository.sumRemainingLeave(empId, joiningDate.getYear(), oneYearDate.getYear());

            monthlyLeavePay = remainingLeave * hourlyWage * contractDailyHour;
        } else if (month == 1 && year > joiningDate.getYear() + 1){
            int remainingLeave = leaveRecordRepository.findRemainingLeaveByEmpIdAndYear(empId, year - 1);
            monthlyLeavePay = remainingLeave * hourlyWage * contractDailyHour;
        }

        // 식대
        int mealAllowance = requestDTO.getDailyMealAllowance() * workHistory.getWorkDays();

        // 연장근로수당
        int overtimePay = (int) (workHistory.getOvertimeWorkHour() * hourlyWage * 1.5);
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
                    int hour = wh.getHolidayWorkHour();

                    int normalHour = Math.min(hour, 8);
                    int overtimeHour = Math.max(hour-8, 0);

                    return (int)(normalHour * hourlyWage * 2.5) +
                            (overtimeHour * hourlyWage * 3);
                }).sum();

        return PayrollHistoryPaymentDTO.builder()
                .basicSalary(basicSalary)
                .weeklyHolidayPay(weeklyHolidayPay)
                .monthlyLeavePay(monthlyLeavePay)
                .overTimePay(overtimePay)
                .holidayWorkPay(holidayWorkPay)
                .mealAllowance(mealAllowance)
                .retroactivePay(retroactivePay)
                .unpaidAllowance(unpaidAllowance)
                .unpaidAllowanceReason(unpaidAllowanceReason)
                .otherAllowance(otherAllowance)
                .build();
    }

    // 미지급수당 사유 검증
    private void validUnpaidAllowance(int unpaidAllowance, String reason){
        if(unpaidAllowance == 0 && StringUtils.hasText(reason)){
            throw new IllegalArgumentException("미지급 수당이 없으면 사유 입력이 불가능합니다.");
        }
        if(unpaidAllowance > 0 && !StringUtils.hasText(reason)){
            throw new IllegalArgumentException("미지급 수당 사유를 입력해주세요.");
        }
    }

    // 공제항목 계산
    private PayrollHistoryDeductionDTO calculateDeduction(PayrollRequestDTO requestDTO, int grossSalary, Employee emp){
        LocalDate date = LocalDate.of(requestDTO.getYear(), requestDTO.getMonth(), 15);

        InsuranceRateHistory insuranceRate = insuranceRateHistoryRepository.findInsuranceRate(date)
                .orElseThrow(() -> new EntityNotFoundException("해당 조건의 공제율이 없습니다."));

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
        int requestYear = requestDTO.getYear();
        int requestMonth = requestDTO.getMonth();

        // 기준소득월액 기준 연도
        int nationalPensionBaseYear = requestDTO.getMonth() >= 7 ? requestYear-1 : requestYear -2;

        // 기준소득월액
        int nationalPensionBase;
        // 국민연금 계산용
        // 국민연금 계산 시 공제율은 1월부터, 기준소득월액은 7월부터 반영
        // 입사일이 전년도 or 전전년도 12월 1일 이후 일 때는 계약시 평균 급여로 계산
        if(joiningDate.isAfter(LocalDate.of(nationalPensionBaseYear, 12, 1))) {
            nationalPensionBase = emp.getInitialMonthlyIncome();
        } else{
            // 기준 연도 총소득액
            int totalSalary = payrollHistoryRepository.sumYearGrossSalary(empId, nationalPensionBaseYear)
                    .orElseThrow(() -> new EntityNotFoundException("해당 연도의 급여 기록이 없습니다."));

            LocalDate baseYearStart = LocalDate.of(nationalPensionBaseYear, 1, 1);

            if (joiningDate.isAfter(baseYearStart)) {
                baseYearStart = joiningDate;
            }

            // 기준 연도 국민연금 가입일수
            int baseYearDays = (int) ChronoUnit.DAYS.between(
                    baseYearStart,
                    LocalDate.of(nationalPensionBaseYear, 12, 31).plusDays(1)
            );
            // 기준소득월액 (1000원 미만 절사)
            nationalPensionBase = (int)(((long) totalSalary * 30 / baseYearDays) / 1000 * 1000);
        }

        // 건강보험 계산
        int healthBaseYear = requestDTO.getMonth() >= 4 ? requestYear-1 : requestYear-2;
        int healthInsuranceBase = calculateHealthInsurance(empId, healthBaseYear, joiningDate, emp);

        // 건강보험정산
        int healthAdjustment;

        if(requestMonth == 4){
            healthAdjustment = calculateHealthAdjustment(empId, requestYear - 1, joiningDate, emp);
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
    private int floorUnderTen(int amount, BigDecimal rate){
        rate = rate.divide(BigDecimal.valueOf(100));
        return BigDecimal.valueOf(amount)
                .multiply(rate)
                .divideToIntegralValue(BigDecimal.TEN)
                .intValue() * 10;
    }

    // 건강보험 계산 함수
    private int calculateHealthInsurance(Long empId, int healthBaseYear, LocalDate joiningDate, Employee emp){
        int healthInsuranceBase;

        if(joiningDate.isAfter(LocalDate.of(healthBaseYear, 12, 1))) {
            healthInsuranceBase = emp.getInitialMonthlyIncome();
        } else{
            int totalSalary = payrollHistoryRepository.sumYearGrossSalary(empId, healthBaseYear)
                    .orElseThrow(() -> new EntityNotFoundException("해당 연도의 급여 기록이 없습니다."));

            LocalDate yearStart = LocalDate.of(healthBaseYear, 1, 1);
            LocalDate yearEnd = LocalDate.of(healthBaseYear, 12, 31);

            LocalDate workStart = joiningDate.isAfter(yearStart)
                    ? joiningDate : yearStart;

            LocalDate retirementDate = emp.getRetirementDate();

            LocalDate workEnd = retirementDate != null && retirementDate.isBefore(yearEnd)
                    ? retirementDate : yearEnd;

            if(workStart.isAfter(workEnd)){
                throw new IllegalArgumentException("해당 연도의 근무 기록이 없습니다.");
            }

            int workMonths = workEnd.getMonthValue() - workStart.getMonthValue() + 1;
            healthInsuranceBase = totalSalary / workMonths;
        }
        return healthInsuranceBase;
    }

    private int calculateHealthAdjustment(Long empId, int previousYear, LocalDate joiningDate, Employee emp){
        // 보험금 부과 시작일
        LocalDate insuranceStart = joiningDate.getDayOfMonth() == 1 ? joiningDate : joiningDate.plusMonths(1).withDayOfMonth(1);

        LocalDate startYear = LocalDate.of(previousYear, 1, 1);
        LocalDate endYear = LocalDate.of(previousYear, 12, 31);

        if(insuranceStart.isBefore(startYear)){
            insuranceStart = startYear;
        } else if(insuranceStart.isAfter(endYear)){
            return 0;
        }

        LocalDate retirementDate = emp.getRetirementDate();

        LocalDate insuranceEnd = retirementDate != null && !retirementDate.isAfter(endYear)
                ? retirementDate.minusDays(1) : endYear;

        if(insuranceStart.isAfter(insuranceEnd)){
            return 0;
        }

        int insuranceMonths = insuranceEnd.getMonthValue() - insuranceStart.getMonthValue() + 1;

        int previousSumHealthInsurance = payrollHistoryRepository.sumYearHealthInsurance(empId, previousYear)
                .orElseThrow(() -> new EntityNotFoundException("건강보험 총액을 구할 수 없습니다."));

        int healthAdjustmentBase = calculateHealthInsurance(empId, previousYear, joiningDate, emp);

        BigDecimal previousRate = insuranceRateHistoryRepository
                .findInsuranceRate(LocalDate.of(previousYear, 12, 15))
                .orElseThrow(() -> new EntityNotFoundException("전년도 공제율이 없습니다."))
                .getHealthInsuranceRate();

        int newSumHealthInsurance =
                floorUnderTen(healthAdjustmentBase, previousRate) * insuranceMonths;

        return newSumHealthInsurance - previousSumHealthInsurance;
    }
}
