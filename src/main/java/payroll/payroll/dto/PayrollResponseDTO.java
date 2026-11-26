package payroll.payroll.dto;

import lombok.Getter;
import payroll.entity.Employee;
import payroll.entity.PayrollHistory;

import java.time.LocalDate;

@Getter
public class PayrollResponseDTO {
    private Long id;
    private String empCode;
    private String empName;
    private int year;
    private int month;
    private int hourlyWage;
    private int basicSalary;
    private int weeklyHolidayPay;
    private int retroactivePay;
    private int deductionAmount;
    private int monthlyLeavePay;
    private int dailyMealAllowance;
    private int overtimePay;
    private int unpaidAllowance;
    private String unpaidAllowanceReason;
    private int otherAllowance;
    private int holidayWorkPay;
    private int nationalPension;
    private int healthInsurance;
    private int longTermCareInsurance;
    private int employmentInsurance;
    private int healthInsuranceAdjustment;
    private int longTermCareAdjustment;
    private int retirementIncomeTax;
    private int retirementLocalIncomeTax;
    private int incomeTax;
    private int localIncomeTax;
    private int grossSalary;
    private int totalDeduction;
    private int netSalary;
    private LocalDate paymentDate;
    private LocalDate confirmedAt;

    public PayrollResponseDTO(PayrollHistory payrollHistory, Employee emp){
        this.id = payrollHistory.getId();
        this.empCode = emp.getCode();
        this.empName = emp.getName();
        this.year = payrollHistory.getYear();
        this.month = payrollHistory.getMonth();
        this.hourlyWage = payrollHistory.getHourlyWage();
        this.basicSalary = payrollHistory.getBasicSalary();
        this.weeklyHolidayPay = payrollHistory.getWeeklyHolidayPay();
        this.retroactivePay = payrollHistory.getRetroactivePay();
        this.deductionAmount = payrollHistory.getDeductionAmount();
        this.monthlyLeavePay = payrollHistory.getMonthlyLeavePay();
        this.dailyMealAllowance = payrollHistory.getDailyMealAllowance();
        this.overtimePay = payrollHistory.getOvertimePay();
        this.unpaidAllowance = payrollHistory.getUnpaidAllowance();
        this.unpaidAllowanceReason = payrollHistory.getUnpaidAllowanceReason();
        this.otherAllowance = payrollHistory.getOtherAllowance();
        this.holidayWorkPay = payrollHistory.getHolidayWorkPay();
        this.nationalPension = payrollHistory.getNationalPension();
        this.healthInsurance = payrollHistory.getHealthInsurance();
        this.longTermCareInsurance = payrollHistory.getLongTermCareInsurance();
        this.employmentInsurance = payrollHistory.getEmploymentInsurance();
        this.healthInsuranceAdjustment = payrollHistory.getHealthInsuranceAdjustment();
        this.longTermCareAdjustment = payrollHistory.getLongTermCareAdjustment();
        this.retirementIncomeTax = payrollHistory.getRetirementIncomeTax();
        this.retirementLocalIncomeTax = payrollHistory.getRetirementLocalIncomeTax();
        this.incomeTax = payrollHistory.getIncomeTax();
        this.localIncomeTax = payrollHistory.getLocalIncomeTax();
        this.grossSalary = payrollHistory.getGrossSalary();
        this.totalDeduction = payrollHistory.getTotalDeduction();
        this.netSalary = payrollHistory.getNetSalary();
        this.paymentDate = payrollHistory.getPaymentDate();
        this.confirmedAt = payrollHistory.getConfirmedAt();
    }
}