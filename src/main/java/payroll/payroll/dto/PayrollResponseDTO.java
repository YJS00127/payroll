package payroll.payroll.dto;

import payroll.entity.PayrollHistory;

import java.time.LocalDate;

public class PayrollResponseDTO {
    private String empCode;
    private String empName;
    private String deptName;
    private int year;
    private int month;
    private int hourlyWage;
    private int basicSalary;
    private int weeklyHolidayPay;
    private int retroactivePay;
    private int deductionAmount;
    private int monthlyLeavePay;
    private int mealAllowance;
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
    private int totalDeductions;
    private int netSalary;
    private LocalDate paymentDate;

    public PayrollResponseDTO(PayrollHistory payrollHistory,
                              String empCode, String empName, String deptName){
        this.empCode = empCode;
        this.empName = empName;
        this.deptName = deptName;
        this.year = payrollHistory.getYear();
        this.month = payrollHistory.getMonth();
        this.hourlyWage = payrollHistory.getHourlyWage();
        this.basicSalary = payrollHistory.getBasicSalary();
        this.weeklyHolidayPay = payrollHistory.getWeeklyHolidayPay();
        this.retroactivePay = payrollHistory.getRetroactivePay();
        this.deductionAmount = payrollHistory.getDeductionAmount();
        this.monthlyLeavePay = payrollHistory.getMonthlyHolidayPay();
        this.mealAllowance = payrollHistory.getMealAllowance();
        this.overtimePay = payrollHistory.getOvertimePay();
        this.unpaidAllowance = payrollHistory.getUnpaidAllowance();
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
        this.totalDeductions = payrollHistory.getTotalDeductions();
        this.netSalary = payrollHistory.getNetSalary();
        this.paymentDate = payrollHistory.getPaymentDate();
    }
}