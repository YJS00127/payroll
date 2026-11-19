package payroll.attendance.dto;

import lombok.Getter;
import payroll.entity.AttendanceRecord;
import payroll.entity.Employee;

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
    private int workMinutes;
    private int overtimeWorkMinutes;
    private int holidayWorkMinutes;
    private String status;


    public AttendanceResponseDTO(AttendanceRecord attendance,
                                 Employee emp) {
        this.id = attendance.getId();
        this.empCode = emp.getCode();
        this.empName = emp.getName();
        this.checkIn = attendance.getCheckIn();
        this.checkOut = attendance.getCheckOut();
        this.workDate = attendance.getWorkDate();
        this.workMinutes = attendance.getWorkMinutes();
        this.overtimeWorkMinutes = attendance.getOvertimeWorkMinutes();
        this.holidayWorkMinutes = attendance.getHolidayWorkMinutes();
        this.status = attendance.getStatus();
    }
}
