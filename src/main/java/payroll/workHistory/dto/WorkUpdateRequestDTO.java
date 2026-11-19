package payroll.workHistory.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class WorkUpdateRequestDTO {
    private int year;
    private int month;
    private String empName;
    private String empCode;
    private int workDays;
    private int totalWorkHour;
    private int overtimeWorkHour;
    private int holidayWorkHour;
    private int weeklyHolidayDays;
}
