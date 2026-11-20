package payroll.salaryHistory.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import payroll.entity.SalaryHistory;

import java.time.LocalDate;

@Getter
public class SalaryHistoryRequestDTO {
    private Long empId;
    private int previousHourlyWage;
    @NotNull(message="변경될 시급을 입력해주세요")
    private int newHourlyWage;
    @NotNull(message="변경된 시급 적용일을 입력해주세요")
    private LocalDate effectiveDate;
    @NotNull(message="변경 사유를 입력해주세요")
    private String reason;

    public SalaryHistory toEntity(){
        return SalaryHistory.builder()
                .empId(this.empId)
                .previousHourlyWage(this.previousHourlyWage)
                .newHourlyWage(this.newHourlyWage)
                .effectiveDate(this.effectiveDate)
                .reason(this.reason)
                .build();
    }
}
