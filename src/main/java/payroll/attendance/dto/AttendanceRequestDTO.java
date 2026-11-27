package payroll.attendance.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import payroll.entity.AttendanceRecord;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
public class AttendanceRequestDTO {
    @NotBlank(message = "사원코드를 입력해주세요")
    private String empCode;
//    @Future(message = "근태를 미리 등록할 수 없습니다")
    @NotNull(message = "근로일을 입력해주세요")
    private LocalDate workDate;
    @NotNull(message = "출근 시간을 입력해주세요")
    private LocalTime checkIn;
    @NotNull(message = "퇴근 시간을 입력해주세요")
    private LocalTime checkOut;
    @NotBlank(message = "근로 상태를 입력해주세요")
    private String status;

    public AttendanceRecord toEntity(Long empId, int workMinutes, int overtimeWorkMinutes, int holidayWorkMinutes){
        return AttendanceRecord.builder()
                .empId(empId)
                .workDate(this.workDate)
                .workMinutes(workMinutes)
                .overtimeWorkMinutes(overtimeWorkMinutes)
                .holidayWorkMinutes(holidayWorkMinutes)
                .checkIn(this.checkIn)
                .checkOut(this.checkOut)
                .status(this.status)
                .build();
    }
}
