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
import payroll.entity.*;
import payroll.payroll.dto.*;
import payroll.repository.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PayrollServiceImpl implements PayrollService{

    private final PayrollHistoryRepository payrollHistoryRepository;
    private final EmployeeRepository employeeRepository;
    private final WorkHistoryRepository workHistoryRepository;
    private final LeaveHistoryRepository leaveHistoryRepository;
    private final AttendanceRecordRepository attendanceRecordRepository;
    private final DepartmentRepository departmentRepository;

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
    public PayrollResultDTO savePayroll(PayrollRequestDTO requestDTO){
        Long empId = employeeRepository.findIdByCode(requestDTO.getEmpCode());
        int hourlyWage = employeeRepository.findById(empId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 사원입니다.")).getHourlyWage();
        // 지급항목
        PayrollHistoryPaymentDTO payment = calculatePayment(requestDTO);
        // 급여합계
        int grossSalary = calculatePayment(requestDTO).grossSalary();
        // 공제항목
        PayrollHistoryDeductionDTO deduction = calculateDeduction(requestDTO, grossSalary);
        // 공제합계
        int totalDeduction = calculateDeduction(requestDTO, grossSalary).totalDeduction();
        // 실수령액
        int netSalary = grossSalary - totalDeduction;

        PayrollHistory payroll = PayrollHistory.builder()
                .empId(empId) .hourlyWage(hourlyWage)
                .basicSalary(payment.getBasicSalary()) .weeklyHolidayPay(payment.getWeeklyHolidayPay())
                .retroactivePay(payment.getRetroactivePay()) .deductionAmount(payment.getDeductionAmount())
                .monthlyHolidayPay(payment.getRetroactivePay()) .mealAllowance(payment.getMealAllowance())
                .overtimePay(payment.getOverTimePay()) .unpaidAllowance(payment.getUnpaidAllowance())
                .unpaidAllowanceReason(payment.getUnpaidAllowanceReason()) .otherAllowance(payment.getOtherAllowance())
                .holidayWorkPay(payment.getHolidayWorkPay())
                .nationalPension(deduction.getNationalPension()) .healthInsurance(deduction.getHealthInsurance())
                .longTermCareInsurance(deduction.getLongTermCareInsurance()) .employmentInsurance(deduction.getEmploymentInsurance())
                .healthInsuranceAdjustment(deduction.getHealthInsuranceAdjustment()) .longTermCareAdjustment(deduction.getLongTermCareAdjustment())
                .retirementIncomeTax(deduction.getRetirementIncomeTax()) .retirementLocalIncomeTax(deduction.getRetirementLocalIncomeTax())
                .incomeTax(deduction.getIncomeTax()) .localIncomeTax(deduction.getLocalIncomeTax())
                .grossSalary(grossSalary) .totalDeductions(totalDeduction).netSalary(netSalary)
                .build();

        if(payrollHistoryRepository.existsByEmpIdAndYearAndMonth(payroll.getEmpId(), payroll.getYear(), payroll.getMonth())){
            throw new EntityExistsException("해당 날짜의 사원 급여 기록이 이미 존재합니다.");
        }

        payrollHistoryRepository.save(payroll);

        return new PayrollResultDTO("success", "급여 정보 저장 성공");
    }

    @Override
    @Transactional
    public PayrollResultDTO updatePayroll(PayrollRequestDTO requestDTO){
        Long empId = employeeRepository.findIdByCode(requestDTO.getEmpCode());

        // 지급항목
        PayrollHistoryPaymentDTO payment = calculatePayment(requestDTO);
        // 급여합계
        int grossSalary = calculatePayment(requestDTO).grossSalary();
        // 공제항목
        PayrollHistoryDeductionDTO deduction = calculateDeduction(requestDTO, grossSalary);
        // 공제합계
        int totalDeduction = calculateDeduction(requestDTO, grossSalary).totalDeduction();
        int netSalary = grossSalary - totalDeduction;

        PayrollHistory payrollHistory = payrollHistoryRepository.findById(empId)
                        .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 급여 기록입니다."));

        payrollHistory.update(payment, deduction, grossSalary, totalDeduction, netSalary);

        return new PayrollResultDTO("success", "급여 정보 수정 성공");
    }

    private Specification<PayrollHistory> createSpec(PayrollSearchDTO searchDTO){
        return(root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.equal(root.get("deleteYn"), 'N'));

            if(StringUtils.hasText(searchDTO.getEmpCode())){
                predicates.add(cb.equal(root.get("empCode"), searchDTO.getEmpCode()));
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
    private PayrollHistoryPaymentDTO calculatePayment(PayrollRequestDTO requestDTO){
        Long empId = employeeRepository.findIdByCode(requestDTO.getEmpCode());

        int year = requestDTO.getYear();;
        int month = requestDTO.getMonth();
        int paymentDate = 15;
        LocalDate start = LocalDate.of(year, month, paymentDate).minusMonths(1);
        LocalDate end = LocalDate.of(year, month, paymentDate);

        // empId에 해당하는 사원 db 정보
        Employee emp = employeeRepository.findById(empId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 사원입니다."));

        // 특정 사원의 특정 달 근로 정보 (총 근로시간, 총 근로일, 주차일수, 주차시간 등)
        WorkHistory work = workHistoryRepository.findByEmpIdAndYearAndMonth(empId, year, month)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 근로 기록입니다."));

        // 특정 사원의 특정 달 휴가 정보 (총 휴가, 사용 휴가, 남은 휴가)
        LeaveRecord leave = leaveHistoryRepository.findByEmpIdAndYearAndMonth(empId, year, month)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 휴가 정보입니다."));

        // 휴일 근로 수당 계산을 위한 특정 사원, 특정 달의 일단위 근로 정보
        List<AttendanceRecord> attendance = attendanceRecordRepository.findByEmpIdPeriod(empId, start, end);


        // 시급
        int hourlyWage = emp.getHourlyWage();
        // 총 근로시간
        int totalWorkHour = work.getTotalWorkHour();
        // 총 근로일 수
        int workDays = work.getWorkDays();
        // 주차시간
        int weeklyHolidayHour = work.getWeeklyHolidayDays() * 8;
        // 미사용 월차
        int remainingLeave = leave.getRemainingLeave();
        // 일일식대
        int dailyMealAllowance = requestDTO.getDailyMealAllowance();
        // 연장근로 시간
        int overtimeWorkHour = work.getOvertimeWorkHour();


        // 기본 급여 계산 (총 근로 시간 * 시급)
        int basicSalary = hourlyWage * totalWorkHour;
        // 주차수당 계산 (주차 시간 * 시급)
        int weeklyHolidayPay = weeklyHolidayHour * hourlyWage;
        // 월차수당 계산 (미사용 월차 *  일급)
        int monthlyLeavePay = remainingLeave * hourlyWage * 8;
        // 식대 계산 (일일 식대 * 근로일 수)
        int mealAllowance = dailyMealAllowance * workDays;
        // 연장근로수당 계산 (연장근로시간 * 시급 * 1.5)
        int overTimePay = (int)(overtimeWorkHour * hourlyWage * 1.5);
        // 전월소급분
        int retroactivePay = requestDTO.getRetroactivePay();
        // 미지급수당, 미지급수당 사유 검증 (미지급수당 존재 시 사유 필수)
        int unpaidAllowance = requestDTO.getUnpaidAllowance();
        String unpaidAllowanceReason = requestDTO.getUnpaidAllowanceReason();
        validUnpaidAllowance(unpaidAllowance, unpaidAllowanceReason);
        // 기타수당
        int otherAllowance = requestDTO.getOtherAllowance();

        // 휴일근로수당 계산 (8시간 미만 : 시급*1.5 / 8시간 이상 : 8시간 치 급여(8*시급*1.5) + 초과 근로 시간*2
        int holidayWorkPay = (int)attendance.stream()
                .filter(record -> {
                    LocalDate date = record.getWorkDate();
                    return date.getDayOfWeek() == DayOfWeek.SUNDAY;
                })
                .mapToInt(record -> {
                    int workHour = record.getWorkHour();

                    int normalHour = Math.min(8, workHour);
                    int extraHour = Math.max(workHour-8, 0);

                    return (int)(normalHour * hourlyWage * 1.5) + extraHour * hourlyWage*2;
                })
                .sum();

        return PayrollHistoryPaymentDTO.builder()
                .basicSalary(basicSalary)
                .weeklyHolidayPay(weeklyHolidayPay)
                .monthlyLeavePay(monthlyLeavePay)
                .overTimePay(overTimePay)
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
    private PayrollHistoryDeductionDTO calculateDeduction(PayrollRequestDTO requestDTO, int grossSalary){
        // 계약직평균급여
        int averagePayroll = requestDTO.getAveragePayroll();
        // 국민연금 공제 비율
        double nationalPensionRate = 4.75;
        // 건강보험 공제 비율
        double healthInsuranceRate = 3.595;
        // 노인장기요양 공제 비율
        double longTermCareInsuranceRate = 13.14;
        // 고용보험 공제 비율
        double employmentInsuranceRate = 0.9;
        // 주민세 공제 비율
        double localIncomeTaxRate = 10;


        // 국민연금 계산 (계약직평균급여 * 국민연금 공제 비율)
        int nationalPension = floorUnderTen(averagePayroll, nationalPensionRate);
        // 건강보험 계산 (계약직평균급여 * 건강보험 공제 비율)
        int healthInsurance = floorUnderTen(averagePayroll, healthInsuranceRate);
        // 노인장기요양 계산 (건강보험 공제금 * 노인장기요양 공제 비율)
        int longTermCareInsurance = floorUnderTen(healthInsurance, longTermCareInsuranceRate);
        // 고용보험 계산 (급여합계 * 고용보험 공제 비율)
        int employmentInsurance = floorUnderTen(grossSalary, employmentInsuranceRate);
        // 건강보험정산
        int healthInsuranceAdjustment = requestDTO.getHealthInsuranceAdjustment();
        // 요양보험정산 계산 (건강보험정산 * 노양장기요양 공제 비율)
        int longTermCareAdjustment = floorUnderTen(healthInsuranceAdjustment, longTermCareInsuranceRate);
        // 퇴직정산소득세
        int retirementIncomeTax = requestDTO.getRetirementIncomeTax();
        // 퇴직정산주민세
        int retirementLocalIncomeTax = requestDTO.getRetirementLocalIncomeTax();
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
    private int floorUnderTen(int amount, double rate){
        rate = rate/100;
        return (int)((int)(amount * rate/10) * 10);
    }
}
