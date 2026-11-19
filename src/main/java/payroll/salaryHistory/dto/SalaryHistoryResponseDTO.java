package payroll.salaryHistory.dto;

import lombok.Getter;
import payroll.entity.SalaryHistory;

import java.time.LocalDate;

@Getter
public class SalaryHistoryResponseDTO {
    private Long id;
    private String empCode;
    private String empName;
    private int previousHourlyWage;
    private int newHourlyWage;
    private LocalDate effectiveDate;
    private String reason;

    public SalaryHistoryResponseDTO(SalaryHistory salaryHistory,
                                    String empCode, String empName){
        this.id = salaryHistory.getId();
        this.empCode = empCode;
        this.empName = empName;
        this.previousHourlyWage = salaryHistory.getPreviousHourlyWage();
        this.newHourlyWage = salaryHistory.getNewHourlyWage();
        this.effectiveDate = salaryHistory.getEffectiveDate();
        this.reason = salaryHistory.getReason();
    }
}
