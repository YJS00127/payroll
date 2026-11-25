package payroll.employee.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class EmpUpdateRequestDTO {
    @NotBlank(message = "부서명을 입력해주세요")
    private String deptName;
    @NotBlank(message = "사원명을 입력해주세요")
    private String name;
    @NotBlank(message = "사원 코드를 입력해주세요")
    private String code;
    @NotNull(message = "생년월일을 입력해주세요")
    private LocalDate birth;
    @NotBlank(message = "직급을 입력해주세요")
    private String rank;
    @NotNull(message = "입사일을 입력해주세요")
    private LocalDate joiningDate;
    @NotNull(message = "성별을 입력해주세요")
    private String gender;
    private LocalDate createdAt;
    private LocalDate retirementDate;
    @NotNull(message = "소정근로시간을 입력해주세요")
    private float contractDailyWorkHour;
    @NotNull(message = "월평균 급여를 입력해주세요")
    private int initialMonthlyIncome;
}
