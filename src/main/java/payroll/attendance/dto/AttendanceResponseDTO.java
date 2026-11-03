package payroll.attendance.dto;


import payroll.entity.AttendanceRecord;

import java.time.LocalTime;

public class AttendanceResponseDTO {
    private String empCode;
    private String empName;
    private LocalTime checkIn;
    private LocalTime checkOut;
    private int workHour;
    private int overTimeWorkHour;
    private int holidayWorkHour;

    public AttendanceResponseDTO(AttendanceRecord attendance,
                                 String empCode, String empName){
        this.empCode = empCode;
        this.empName = empName;
        this.checkIn = attendance.getCheckIn();
        this.checkOut = attendance.getCheckOut();
        this.workHour = attendance.getWorkHour();
        this.overTimeWorkHour = attendance.getOvertimeWorkHour();
        this.holidayWorkHour = attendance.getHolidayWorkHour();
    }
}
