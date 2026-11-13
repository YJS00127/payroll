package payroll.employee.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmpSearchDTO {
    private String deptName;
    private String keyword;
    private String rank;
}
