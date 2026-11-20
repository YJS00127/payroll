package payroll.employee.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import payroll.entity.Employee;

import java.time.LocalDate;

@Getter
public class EmpRequestDTO {
    @NotBlank(message = "부서명을 입력해주세요")
    private String deptName;
    @NotBlank(message = "사원명을 입력해주세요")
    private String name;
    @NotBlank(message = "사원코드를 입력해주세요")
    private String code;
    @NotNull(message = "생년월일을 입력해주세요")
    private LocalDate birth;
    @NotBlank(message = "직급을 입력해주세요")
    private String rank;
    @NotNull(message = "입사일을 입력해주세요")
    private LocalDate joiningDate;
    @NotNull(message = "성별을 입력해주세요")
    private String gender;
    private String deleteYn;
    private LocalDate createdAt;
    @NotNull(message = "시급을 입력해주세요")
    @Max(value=80000, message="시급을 다시 입력해주세요")
    @Min(value=0, message = "시급은 음수일 수 없습니다.")
    private int hourlyWage;
    private LocalDate retirementDate;
    @NotNull(message="1일 근로시간을 입력해주세요")
    @Min(value = 1, message = "1일 근로시간을 다시 입력해주세요")
    private int contractDailyWorkMinutes;
    @NotNull(message="계약 평균 급여를 입력해주세요")
    private int initialMonthlyIncome;

    public Employee toEntity(Long deptId){
        return Employee.builder()
                .deptId(deptId)
                .name(this.name)
                .code(this.code)
                .birth(this.birth)
                .rank(this.rank)
                .joiningDate(this.joiningDate)
                .gender(this.gender)
                .hourlyWage(this.hourlyWage)
                .retirementDate(this.retirementDate)
                .contractDailyWorkMinutes(this.contractDailyWorkMinutes)
                .initialMonthlyIncome(this.initialMonthlyIncome)
                .build();
    }
}
