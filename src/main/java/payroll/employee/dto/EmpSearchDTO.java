package payroll.employee.dto;

import lombok.Getter;

import java.time.LocalDate;

@Getter
public class EmpSearchDTO {
    private String deptCode;
    private String name;
    private String code;
    private String rank;
    private LocalDate joiningDate;
}
