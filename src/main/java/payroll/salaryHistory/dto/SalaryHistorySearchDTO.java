package payroll.salaryHistory.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SalaryHistorySearchDTO {
    private String keyword;
    private Integer year;
    private Integer month;
}
