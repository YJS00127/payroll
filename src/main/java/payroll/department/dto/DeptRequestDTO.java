package payroll.department.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import payroll.entity.Department;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class DeptRequestDTO {
    @NotBlank(message="부서명은 필수 입력값입니다")
    @Size(max = 50)
    private String name;

    @NotBlank(message="부서코드는 필수 입력값입니다")
    @Size(max = 50)
    private String code;

    @NotNull(message="부서생성일은 필수 입력값입니다")
    private LocalDate createdDeptDate;

    public Department toEntity(){
        return Department.builder()
                .name(this.name)
                .code(this.code)
                .createdDeptDate(this.createdDeptDate)
                .build();
    }
}
