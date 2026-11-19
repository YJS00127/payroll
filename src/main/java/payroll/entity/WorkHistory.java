package payroll.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@Getter
@Entity
@Table(name = "work_history")
@NoArgsConstructor
public class WorkHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "work_record_seq")
    @SequenceGenerator(name = "work_record_seq", sequenceName = "work_record_seq", allocationSize = 1)
    private Long id;

    @Column(name = "emp_id")
    private Long empId;

    @Column(name = "year")
    private int year;

    @Column(name = "month")
    private int month;

    @Comment("총 근로 일수")
    @Column(name = "work_days")
    private int workDays;

    @Comment("총 근로 시간")
    @Column(name = "total_work_minutes")
    private int totalWorkMinutes;

    @Comment("연장근로 시간")
    @Column(name = "overtime_work_minutes")
    private int overtimeWorkMinutes;

    @Comment("휴일근로 시간")
    @Column(name = "holiday_work_minutes")
    private int holidayWorkMinutes;

    @Comment("주차 일수")
    @Column(name = "weekly_holiday_days")
    private int weeklyHolidayDays;

    @Builder
    public WorkHistory(Long empId, int year, int month, int totalWorkMinutes, int workDays,
                       int overtimeWorkMinutes, int holidayWorkMinutes, int weeklyHolidayDays) {
        this.empId = empId;
        this.year = year;
        this.month = month;
        this.totalWorkMinutes = totalWorkMinutes;
        this.workDays = workDays;
        this.overtimeWorkMinutes = overtimeWorkMinutes;
        this.holidayWorkMinutes = holidayWorkMinutes;
        this.weeklyHolidayDays = weeklyHolidayDays;
    }

    public void add(AttendanceRecord attendanceRecord){
        if(attendanceRecord.getWorkMinutes() > 0 ||
                attendanceRecord.getOvertimeWorkMinutes() > 0 ||
                attendanceRecord.getHolidayWorkMinutes() > 0){
            this.workDays++;
        }

        this.totalWorkMinutes += attendanceRecord.getWorkMinutes();
        this.overtimeWorkMinutes += attendanceRecord.getOvertimeWorkMinutes();
        this.holidayWorkMinutes += attendanceRecord.getHolidayWorkMinutes();
    }

    public void update(int workDays, int totalWorkMinutes, int overtimeWorkMinutes,
                       int holidayWorkMinutes, int weeklyHolidayDays){
        this.workDays = workDays;
        this.totalWorkMinutes = totalWorkMinutes;
        this.overtimeWorkMinutes = overtimeWorkMinutes;
        this.holidayWorkMinutes = holidayWorkMinutes;
        this.weeklyHolidayDays = weeklyHolidayDays;
    }

    public void updateWeeklyHolidayDays(int weeklyHolidayDays){
        this.weeklyHolidayDays = weeklyHolidayDays;
    }
}
