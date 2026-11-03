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
    @NotBlank
    @Size(max = 50)
    private String name;

    @NotBlank
    @Size(max = 50)
    private String code;

    @NotNull
    private LocalDate createdDeptDate;

    public Department toEntity(){
        return Department.builder()
                .name(this.name)
                .code(this.code)
                .createdDeptDate(this.createdDeptDate)
                .build();
    }
}
