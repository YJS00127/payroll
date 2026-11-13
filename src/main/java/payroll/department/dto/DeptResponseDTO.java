package payroll.department.dto;

import java.lang.String;
import java.time.LocalDate;

import lombok.Getter;
import lombok.NoArgsConstructor;
import payroll.entity.Department;

@NoArgsConstructor
@Getter
public class DeptResponseDTO {
    private Long id;
    private String name;
    private String code;
    private String deleteYn;
    private LocalDate createdAt;
    private LocalDate createdDeptDate;

    public DeptResponseDTO(Department department){
        this.id = department.getId();
        this.name = department.getName();
        this.code = department.getCode();
        this.deleteYn = department.getDeleteYn();
        this.createdAt = department.getCreatedAt();
        this.createdDeptDate = department.getCreatedDeptDate();
    }
}
