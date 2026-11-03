package payroll.employee.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import payroll.entity.Employee;

import java.time.LocalDate;

@Getter
public class EmpRequestDTO {
    @NotBlank(message = "부서 코드는 필수 입력값입니다.")
    private String deptCode;
    @NotBlank(message = "이름은 필수 입력값입니다.")
    private String name;
    @NotBlank(message = "사원코드는 필수 입력값입니다.")
    private String code;
    @NotNull(message = "생년월일은 필수 입력값입니다.")
    private LocalDate birth;
    @NotBlank(message = "직급은 필수 입력값입니다.")
    private String rank;
    @NotNull(message = "입사일은 필수 입력값입니다.")
    private LocalDate joiningDate;
    private String deleteYn;
    private LocalDate createdAt;
    @NotNull(message = "시급은 필수 입력값입니다.")
    @Min(value = 0, message = "시급은 음수일 수 없습니다.")
    private int hourlyWage;

    public Employee toEntity(Long deptId){
        return Employee.builder()
                .deptId(deptId)
                .name(this.name)
                .code(this.code)
                .birth(this.birth)
                .rank(this.rank)
                .joiningDate(this.joiningDate)
                .hourlyWage(this.hourlyWage)
                .build();
    }
}
