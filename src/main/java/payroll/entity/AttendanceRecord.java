package payroll.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.annotations.Comment;
import payroll.attendance.dto.AttendanceRequestDTO;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Entity
@Table(name = "attendance_record")
public class AttendanceRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "attendance_record_seq")
    @SequenceGenerator(name = "attendance_record_seq", sequenceName = "attendance_record_seq", allocationSize = 1)
    private Long id;

    @Comment("사원 ID")
    @Column(name = "emp_id")
    private Long empId;

    @Comment("근로일")
    @Column(name = "work_date")
    private LocalDate workDate;

    @Comment("출근 시간")
    @Column(name = "check_in")
    private LocalTime checkIn;

    @Comment("퇴근 시간")
    @Column(name = "check_out")
    private LocalTime checkOut;

    @Comment("일별 근로 시간")
    @Column(name = "work_hour")
    private int workHour;

    @Comment("연장근로 시간")
    @Column(name = "overtime_work_hour")
    private int overtimeWorkHour;

    @Comment("휴일근로 시간")
    @Column(name = "holiday_work_hour")
    private int holidayWorkHour;

    @Comment("근로 상태")
    @Column(name = "status")
    private String status;

    @Builder
    public AttendanceRecord(Long empId, LocalDate workDate, LocalTime checkIn,
                            LocalTime checkOut, String status){
        this.empId = empId;
        this.workDate = workDate;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.status = status;
    }

    public void update(AttendanceRequestDTO requestDTO, Long empId){
        this.empId = empId;
        this.workDate = requestDTO.getWorkDate();
        this.checkIn = requestDTO.getCheckIn();
        this.checkOut = requestDTO.getCheckOut();
        this.status = requestDTO.getStatus();

        calculateWorkHour(requestDTO.getWorkDate());
    }


    // 근로 시간 정보 저장 (일일 근로 시간, 연장 근로 시간, 휴일 근로 시간)
    private void calculateWorkHour(LocalDate workDate){
        // 특정 날짜 요일 저장
        DayOfWeek dayOfWeek = workDate.getDayOfWeek();

        // 일일 총 근로 시간
        int dailyWorkHour = (int) Duration.between(
                checkIn, checkOut
        ).toHours() - 1;

        // 주말이 아닐 시 일일 근로 시간, 연장근로 시간 저장 / 주말일 시 휴일 근로 시간 저장
        if(dayOfWeek.getValue() != 6 && dayOfWeek.getValue() != 7){
            this.overtimeWorkHour = Math.max(0, dailyWorkHour - 8);
            this.workHour = dailyWorkHour - overtimeWorkHour;
        } else {
            this.holidayWorkHour = dailyWorkHour;
        }
    }
}
