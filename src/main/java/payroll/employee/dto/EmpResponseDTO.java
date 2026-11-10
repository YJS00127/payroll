package payroll.employee.dto;

import lombok.Getter;
import payroll.entity.Employee;

import java.time.LocalDate;

@Getter
public class EmpResponseDTO {
    private Long id;
    private String deptCode;
    private String name;
    private String code;
    private String rank;
    private LocalDate joiningDate;
    private String deleteYn;
    private LocalDate createdAt;

    public EmpResponseDTO(Employee employee, String deptCode){
        this.id = employee.getId();
        this.deptCode = deptCode;
        this.name = employee.getName();
        this.code = employee.getCode();
        this.rank = employee.getRank();
        this.joiningDate = employee.getJoiningDate();
        this.deleteYn = employee.getDeleteYn();
        this.createdAt = employee.getCreatedAt();
    }
}
