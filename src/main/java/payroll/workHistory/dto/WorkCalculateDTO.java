package payroll.workHistory.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class WorkCalculateDTO {
    private int workDays;
    private int totalWorkHour;
    private int overtimeWorkHour;
    private int holidayWorkHour;
    private int weeklyHolidayDays;
}
