package payroll.payroll.dto;

import lombok.Getter;
import payroll.entity.Employee;
import payroll.entity.InsuranceRateHistory;
import payroll.entity.WorkHistory;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
public class PayslipPDFResponseDTO {

    private PayrollResponseDTO payroll;

    private Long empId;
    private LocalDate empBirth;
    private String deptName;
    private String empRank;
    private String empGender;
    private boolean isRetirement;

    private double workDays;
    private double weeklyHolidayDays;
    private double totalWorkHour;
    private double weeklyHolidayHour;
    private double deductionHour;

    private BigDecimal nationalPensionRate;
    private BigDecimal healthInsuranceRate;
    private BigDecimal longTermCareInsuranceRate;
    private BigDecimal employmentInsuranceRate;
    private BigDecimal localIncomeTaxRate;

    private int pensionBase;
    private int healthBase;

    public PayslipPDFResponseDTO(PayrollResponseDTO payroll, Employee emp, WorkHistory workHistory, InsuranceRateHistory insuranceRateHistory, String deptName,
                                 int pensionBase, int healthBase, boolean isRetirement){
        this.payroll = payroll;
        this.empId = emp.getId();
        this.empBirth = emp.getBirth();
        this.deptName = deptName;
        this.empRank = emp.getRank();
        this.empGender = emp.getGender();
        this.isRetirement = isRetirement;
        this.workDays = workHistory.getWorkDays();
        this.weeklyHolidayDays = workHistory.getWeeklyHolidayDays();
        this.totalWorkHour= (double) workHistory.getTotalWorkMinutes()/60;
        this.weeklyHolidayHour = (double) (workHistory.getWeeklyHolidayDays() * emp.getContractDailyWorkMinutes())/60;
        this.deductionHour = (double) workHistory.getDeductionMinutes()/60;
        this.nationalPensionRate = insuranceRateHistory.getNationalPensionRate();
        this.healthInsuranceRate = insuranceRateHistory.getHealthInsuranceRate();
        this.longTermCareInsuranceRate = insuranceRateHistory.getLongTermCareInsuranceRate();
        this.employmentInsuranceRate = insuranceRateHistory.getEmploymentInsuranceRate();
        this.localIncomeTaxRate = insuranceRateHistory.getLocalIncomeTaxRate();
        this.pensionBase = pensionBase;
        this.healthBase = healthBase;
    }
}
