package payroll.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import payroll.attendance.dto.AttendanceRequestDTO;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Entity
@Table(name = "attendance_record")
@NoArgsConstructor
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
    @Column(name = "work_minutes")
    private int workMinutes;

    @Comment("연장근로 시간")
    @Column(name = "overtime_work_minutes")
    private int overtimeWorkMinutes;

    @Comment("휴일근로 시간")
    @Column(name = "holiday_work_minutes")
    private int holidayWorkMinutes;

    @Comment("공제 시간")
    @Column(name = "deduction_minutes")
    private int deductionMinutes;

    @Comment("근로 상태")
    @Column(name = "status")
    private String status;

    @Builder
    public AttendanceRecord(Long empId, LocalDate workDate, LocalTime checkIn, LocalTime checkOut, int workMinutes,
                            int overtimeWorkMinutes, int holidayWorkMinutes, String status, int deductionMinutes){
        this.empId = empId;
        this.workDate = workDate;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.workMinutes = workMinutes;
        this.overtimeWorkMinutes = overtimeWorkMinutes;
        this.holidayWorkMinutes = holidayWorkMinutes;
        this.deductionMinutes = deductionMinutes;
        this.status = status;
    }

    public void update(AttendanceRequestDTO requestDTO, Long empId, int workMinutes,
                       int overtimeWorkMinutes, int holidayWorkMinutes, int deductionMinutes) {
        this.empId = empId;
        this.workDate = requestDTO.getWorkDate();
        this.checkIn = requestDTO.getCheckIn();
        this.checkOut = requestDTO.getCheckOut();
        this.status = requestDTO.getStatus();
        this.workMinutes = workMinutes;
        this.overtimeWorkMinutes = overtimeWorkMinutes;
        this.holidayWorkMinutes = holidayWorkMinutes;
        this.deductionMinutes = deductionMinutes;
    }
}
