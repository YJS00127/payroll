package payroll.employee.dto;

import lombok.Getter;
import payroll.entity.Employee;

import java.time.LocalDate;

@Getter
public class EmpResponseDTO {
    private Long id;
    private String name;
    private String code;
    private String deptName;
    private LocalDate birth;
    private String rank;
    private String gender;
    private LocalDate joiningDate;
    private int hourlyWage;
    private int initialMonthlyIncome;
    private int contractDailyWorkHour;

    public EmpResponseDTO(Employee employee, String deptName){
        this.id = employee.getId();
        this.name = employee.getName();
        this.code = employee.getCode();
        this.deptName = deptName;
        this.birth = employee.getBirth();
        this.rank = employee.getRank();
        this.gender = employee.getGender();
        this.joiningDate = employee.getJoiningDate();
        this.hourlyWage = employee.getHourlyWage();
        this.initialMonthlyIncome = employee.getInitialMonthlyIncome();
        this.contractDailyWorkHour = employee.getContractDailyWorkHour();
    }
}
