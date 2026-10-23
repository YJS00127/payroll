package payroll.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.Comment;

@Entity
@Table(name = "payroll_records")
public class PayrollRecord {

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

    @Comment("기본 급여")
    @Column(name = "basic_salary")
    private int basicSalary;

    @Comment("주차수당")
    @Column(name = "weekly_holiday_pay")
    private int weeklyHolidayPay;

    @Comment("전일월소급분")
    @Column(name = "retroactive_pay")
    private int retroactivePay;

    @Comment("공제금")
    @Column(name = "deduction_amount")
    private int deductionAmount;

    @Comment("월차 수당")
    @Column(name = "monthly_holiday_hours")
    private int monthlyHolidayHours;

    @Comment("식대")
    @Column(name = "meal_allowance")
    private int mealAllowance;

    @Comment("연장근로수당")
    @Column(name = "overtime_pay")
    private int overtimePay;

    @Comment("미지급수당")
    @Column(name = "unpaid_allowance")
    private int unpaidAllowance;

    @Comment("기타 수당")
    @Column(name = "other_allowance")
    private int otherAllowance;

    @Comment("휴일 근로 수당")
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

    @Comment("총지급액")
    @Column(name = "gross_salary")
    private int grossSalary;

    @Comment("총공제액")
    @Column(name = "total_deductions")
    private int totalDeductions;

    @Comment("실수령액")
    @Column(name = "net_salary")
    private int netSalary;
}
