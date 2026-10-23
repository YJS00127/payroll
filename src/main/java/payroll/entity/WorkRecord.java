package payroll.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.Comment;

@Entity
@Table(name = "work_records")
public class WorkRecord {

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
    @Column(name = "work_hours")
    private int workHours;

    @Comment("연장근로 시간")
    @Column(name = "overtime_hours")
    private int overtimeHours;

    @Comment("휴일근로 시간")
    @Column(name = "holiday_work_hours")
    private int holidayWorkHours;

    @Comment("주차 일수")
    @Column(name = "weekly_holiday_days")
    private int weeklyHolidayDays;

    @Comment("주차 시간")
    @Column(name = "weekly_holiday_hours")
    private int weeklyHolidayHours;

    @Comment("근무 기록 삭제 여부")
    @Column(name = "delete_yn")
    private String deleteYn;

}
