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
    @NotBlank(message="부서명을 입력해주세요")
    @Size(max = 20, message="부서명을 다시 입력해주세요")
    private String name;

    @NotBlank(message="부서코드를 입력해주세요")
    @Size(max = 20, message="부서코드를 다시 입력해주세요")
    private String code;

    @NotNull(message="부서생성일을 입력해주세요")
    private LocalDate createdDeptDate;

    public Department toEntity(){
        return Department.builder()
                .name(this.name)
                .code(this.code)
                .createdDeptDate(this.createdDeptDate)
                .build();
    }
}
