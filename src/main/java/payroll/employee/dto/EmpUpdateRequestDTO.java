package payroll.employee.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class EmpUpdateRequestDTO {
    @NotBlank(message = "부서 코드는 필수 입력값입니다.")
    private String deptCode;
    @NotBlank(message = "이름은 필수 입력값입니다.")
    private String name;
    @NotBlank(message = "사원 코드는 필수 입력값입니다.")
    private String code;
    @NotNull(message = "생일은 필수 입력값입니다.")
    private LocalDate birth;
    @NotBlank(message = "직급은 필수 입력값입니다.")
    private String rank;
    @NotNull(message = "입사일은 필수 입력값입니다.")
    private LocalDate joiningDate;
    private String deleteYn;
    private LocalDate createdAt;
    @NotNull(message = "시급은 필수 입력값입니다.")
    @Min(value = 0, message = "시급은 음수가될 수 없습니다.")
    private int hourlyWage;
    private String reason;
    private LocalDate effectiveDate;
}
