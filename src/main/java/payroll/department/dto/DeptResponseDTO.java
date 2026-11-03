package payroll.department.dto;

import java.lang.String;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import payroll.entity.Department;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class DeptResponseDTO {
    private Long id;
    private String name;
    private String code;
    private String deleteYn;
    private LocalDate created_at;
    private LocalDate created_dept_date;

    public DeptResponseDTO(Department department){
        this.id = department.getId();
        this.name = department.getName();
        this.code = department.getCode();
        this.deleteYn = department.getDeleteYn();
        this.created_at = department.getCreatedAt();
        this.created_dept_date = department.getCreatedDeptDate();
    }
}
