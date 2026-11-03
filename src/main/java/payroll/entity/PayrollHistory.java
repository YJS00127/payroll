package payroll.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.annotations.Comment;
import payroll.payroll.dto.PayrollHistoryDeductionDTO;
import payroll.payroll.dto.PayrollHistoryPaymentDTO;

import java.time.LocalDate;

@Getter
@Entity
@Table(name = "payroll_history")
@Builder
public class PayrollHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "payroll_record_seq")
    @SequenceGenerator(name = "payroll_record_seq", sequenceName = "payroll_record_seq", allocationSize = 1)
    private Long id;

    @Column(name = "emp_id")
    private Long empId;

    @Column(name = "year")
    private int year;

    @Column(name = "month")
    private int month;

    @Comment("시급")
    @Column(name = "hourly_wage")
    private int hourlyWage;

    @Comment("기본급여")
    @Column(name = "basic_salary")
    private int basicSalary;

    @Comment("주차수당")
    @Column(name = "weekly_holiday_pay")
    private int weeklyHolidayPay;

    @Comment("전월소급분")
    @Column(name = "retroactive_pay")
    private int retroactivePay;

    @Comment("공제금")
    @Column(name = "deduction_amount")
    private int deductionAmount;

    @Comment("월차수당")
    @Column(name = "monthly_holiday_pay")
    private int monthlyHolidayPay;

    @Comment("식대")
    @Column(name = "meal_allowance")
    private int mealAllowance;

    @Comment("연장근로수당")
    @Column(name = "overtime_pay")
    private int overtimePay;

    @Comment("미지급수당")
    @Column(name = "unpaid_allowance")
    private int unpaidAllowance;

    @Comment("미지급수당 사유")
    @Column(name = "unpaid_allowance_reason")
    private String unpaidAllowanceReason;

    @Comment("기타수당")
    @Column(name = "other_allowance")
    private int otherAllowance;

    @Comment("휴일근로수당")
    @Column(name = "holiday_work_pay")
    private int holidayWorkPay;

    @Comment("국민연금")
    @Column(name = "national_pension")
    private int nationalPension;

    @Comment("건강보험")
    @Column(name = "health_insurance")
    private int healthInsurance;

    @Comment("노인장기요양")
    @Column(name = "long_term_care_insurance")
    private int longTermCareInsurance;

    @Comment("고용보험")
    @Column(name = "employment_insurance")
    private int employmentInsurance;

    @Comment("건강보험정산")
    @Column(name = "health_insurance_adjustment")
    private int healthInsuranceAdjustment;

    @Comment("요양보험정산")
    @Column(name = "long_term_care_adjustment")
    private int longTermCareAdjustment;

    @Comment("퇴직정산소득세")
    @Column(name = "retirement_income_tax")
    private int retirementIncomeTax;

    @Comment("퇴직정산주민세")
    @Column(name = "retirement_local_income_tax")
    private int retirementLocalIncomeTax;

    @Comment("소득세")
    @Column(name = "income_tax")
    private int incomeTax;

    @Comment("주민세")
    @Column(name = "local_income_tax")
    private int localIncomeTax;

    @Comment("총 지급액")
    @Column(name = "gross_salary")
    private int grossSalary;

    @Comment("총 공제액")
    @Column(name = "total_deductions")
    private int totalDeductions;

    @Comment("실수령액")
    @Column(name = "net_salary")
    private int netSalary;

    @Comment("급여지급일")
    @Column(name = "payment_date")
    private LocalDate paymentDate;

    @Comment("삭제여부")
    @Column(name = "delete_yn")
    private String deleteYn;

    public void update(PayrollHistoryPaymentDTO payment, PayrollHistoryDeductionDTO deduction,
                       int grossSalary, int totalDeduction ,int netSalary){
        // 지급액
        this.basicSalary = payment.getBasicSalary();
        this.weeklyHolidayPay = payment.getWeeklyHolidayPay();
        this.retroactivePay = payment.getRetroactivePay();
        this.deductionAmount = payment.getDeductionAmount();
        this.monthlyHolidayPay = payment.getRetroactivePay();
        this.mealAllowance = payment.getMealAllowance();
        this.overtimePay = payment.getOverTimePay();
        this.unpaidAllowance = payment.getUnpaidAllowance();
        this.unpaidAllowanceReason = payment.getUnpaidAllowanceReason();
        this.otherAllowance = payment.getOtherAllowance();
        this.holidayWorkPay = payment.getHolidayWorkPay();
        this.grossSalary = grossSalary;
        // 공제액
        this.nationalPension = deduction.getNationalPension();
        this.healthInsurance = deduction.getHealthInsurance();
        this.longTermCareInsurance = deduction.getLongTermCareInsurance();
        this.employmentInsurance = deduction.getEmploymentInsurance();
        this.healthInsuranceAdjustment = deduction.getHealthInsuranceAdjustment();
        this.longTermCareAdjustment = deduction.getLongTermCareAdjustment();
        this.retirementIncomeTax = deduction.getRetirementIncomeTax();
        this.retirementLocalIncomeTax = deduction.getRetirementLocalIncomeTax();
        this.incomeTax = deduction.getIncomeTax();
        this.localIncomeTax = deduction.getLocalIncomeTax();
        this.totalDeductions = totalDeduction;
        // 실수령액
        this.netSalary = netSalary;
    }

    public void delete(){
        this.deleteYn = "Y";
    }
}
