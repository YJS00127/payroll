package payroll.salaryHistory.dto;

import lombok.Getter;

@Getter
public class SalaryHistorySearchDTO {
    private String empCode;
    private Integer year;
    private Integer month;
}
