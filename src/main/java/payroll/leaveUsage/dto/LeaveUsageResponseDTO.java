package payroll.leaveUsage.dto;

import payroll.entity.LeaveUsage;

import java.time.LocalDate;

public class LeaveUsageResponseDTO {
    private String empCode;
    private String empName;
    private LocalDate startDate;
    private LocalDate endDate;
    private int usedDays;

    public LeaveUsageResponseDTO(LeaveUsage leaveUsage, String empCode, String empName){
        this.empCode = empCode;
        this.empName = empName;
        this.startDate = leaveUsage.getStartDate();
        this.endDate = leaveUsage.getEndDate();
        this.usedDays = leaveUsage.getUsedDays();
    }
}
