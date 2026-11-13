package payroll.attendance.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import payroll.entity.AttendanceRecord;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
public class AttendanceRequestDTO {
    private String empName;
    private String empCode;
    @NotNull(message = "근로일은 필수 입력값입니다.")
    private LocalDate workDate;
    @NotNull(message = "출근 시간은 필수 입력값입니다.")
    private LocalTime checkIn;
    @NotNull(message = "퇴근 시간은 필수 입력값입니다.")
    private LocalTime checkOut;
    @NotBlank(message = "근로 상태는 필수 입력값입니다.")
    private String status;

    public AttendanceRecord toEntity(Long empId){
        return AttendanceRecord.builder()
                .empId(empId)
                .workDate(this.workDate)
                .workHour()
                .checkIn(this.checkIn)
                .checkOut(this.checkOut)
                .status(this.status)
                .build();
    }
}
