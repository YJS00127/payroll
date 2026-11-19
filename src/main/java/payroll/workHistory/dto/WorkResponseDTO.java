package payroll.workHistory.dto;

import lombok.Getter;
import payroll.entity.Employee;
import payroll.entity.WorkHistory;

@Getter
public class WorkResponseDTO {
    private Long id;
    private String empCode;
    private String empName;
    private int year;
    private int month;
    private int workDays;
    private int totalWorkMinutes;
    private int overtimeWorkMinutes;
    private int holidayWorkMinutes;
    private int weeklyHolidayDays;

    public WorkResponseDTO(WorkHistory workHistory, Employee emp) {
        this.id = workHistory.getId();
        this.empCode = emp.getCode();
        this.empName = emp.getName();
        this.year = workHistory.getYear();
        this.month = workHistory.getMonth();
        this.workDays = workHistory.getWorkDays();
        this.totalWorkMinutes = workHistory.getTotalWorkMinutes();
        this.overtimeWorkMinutes = workHistory.getOvertimeWorkMinutes();
        this.holidayWorkMinutes = workHistory.getHolidayWorkMinutes();
        this.weeklyHolidayDays = workHistory.getWeeklyHolidayDays();
    }


}