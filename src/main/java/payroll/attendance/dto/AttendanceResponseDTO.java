package payroll.attendance.dto;

import lombok.Getter;
import payroll.entity.AttendanceRecord;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
public class AttendanceResponseDTO {
    private Long id;
    private String empCode;
    private String empName;
    private LocalTime checkIn;
    private LocalTime checkOut;
    private LocalDate workDate;
    private int workHour;
    private String status;


    public AttendanceResponseDTO(AttendanceRecord attendance,
                                 String empCode, String empName) {
        this.id = attendance.getId();
        this.empCode = empCode;
        this.empName = empName;
        this.checkIn = attendance.getCheckIn();
        this.checkOut = attendance.getCheckOut();
        this.workDate = attendance.getWorkDate();
        this.workHour = attendance.getWorkHour();
        this.status = attendance.getStatus();
    }
}
