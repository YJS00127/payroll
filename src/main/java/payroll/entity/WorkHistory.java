package payroll.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.annotations.Comment;

@Getter
@Entity
@Table(name = "work_history")
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
    @Column(name = "total_work_hour")
    private int totalWorkHour;

    @Comment("연장근로 시간")
    @Column(name = "overtime_hour")
    private int overtimeWorkHour;

    @Comment("휴일근로 시간")
    @Column(name = "holiday_work_hour")
    private int holidayWorkHour;

    @Comment("주차 일수")
    @Column(name = "weekly_holiday_days")
    private int weeklyHolidayDays;

    @Builder
    public WorkHistory(Long empId, int year, int month, int totalWorkHour, int workDays,
                       int overtimeWorkHour, int holidayWorkHour, int weeklyHolidayDays) {
        this.empId = empId;
        this.year = year;
        this.month = month;
        this.totalWorkHour = totalWorkHour;
        this.workDays = workDays;
        this.overtimeWorkHour = overtimeWorkHour;
        this.holidayWorkHour = holidayWorkHour;
        this.weeklyHolidayDays = weeklyHolidayDays;
    }

    public void update(int workDays, int totalWorkHour, int overtimeWorkHour,
                       int holidayWorkHour, int weeklyHolidayDays){
        this.workDays = workDays;
        this.totalWorkHour = totalWorkHour;
        this.overtimeWorkHour = overtimeWorkHour;
        this.holidayWorkHour = holidayWorkHour;
        this.weeklyHolidayDays = weeklyHolidayDays;
    }
}
