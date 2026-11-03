package payroll.salaryHistory.dto;

import payroll.entity.SalaryHistory;

import java.time.LocalDate;

public class SalaryHistoryResponseDTO {
    private String empCode;
    private String empName;
    private int previousHourlyWage;
    private int newHourlyWage;
    private LocalDate effectiveDate;
    private String reason;

    public SalaryHistoryResponseDTO(SalaryHistory salaryHistory,
                                    String empCode, String empName){
        this.empCode = empCode;
        this.empName = empName;
        this.previousHourlyWage = salaryHistory.getPreviousHourlyWage();
        this.newHourlyWage = salaryHistory.getNewHourlyWage();
        this.effectiveDate = salaryHistory.getEffectiveDate();
        this.reason = salaryHistory.getReason();
    }
}
