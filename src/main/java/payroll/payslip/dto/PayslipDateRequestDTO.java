package payroll.payslip.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
public class PayslipDateRequestDTO {
    @NotEmpty(message="확정할 날짜를 입력해주세요")
    private List<Long> ids;
    @NotNull(message="급여 지급일을 다시 입력해주세요")
    private LocalDate date;
}
