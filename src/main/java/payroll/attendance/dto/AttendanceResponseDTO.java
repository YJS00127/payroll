package payroll.attendance.dto;


import payroll.entity.AttendanceRecord;

import java.time.LocalDate;
import java.time.LocalTime;

public class AttendanceResponseDTO {
    private String empCode;
    private String empName;
    private LocalTime checkIn;
    private LocalTime checkOut;
    private LocalDate workDate;
    private int workHour;


    public AttendanceResponseDTO(AttendanceRecord attendance,
                                 String empCode, String empName) {
        this.empCode = empCode;
        this.empName = empName;
        this.checkIn = attendance.getCheckIn();
        this.checkOut = attendance.getCheckOut();
        this.workDate = attendance.getWorkDate();
    }
}
