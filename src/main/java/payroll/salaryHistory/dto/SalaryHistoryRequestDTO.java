package payroll.salaryHistory.dto;

import lombok.Getter;
import payroll.entity.SalaryHistory;

import java.time.LocalDate;

@Getter
public class SalaryHistoryRequestDTO {
    private int previousHourlyWage;
    private int newHourlyWage;
    private LocalDate effectiveDate;
    private String reason;

    public SalaryHistory toEntity(){
        return SalaryHistory.builder()
                .previousHourlyWage(this.previousHourlyWage)
                .newHourlyWage(this.newHourlyWage)
                .effectiveDate(this.effectiveDate)
                .reason(this.reason)
                .build();
    }
}
