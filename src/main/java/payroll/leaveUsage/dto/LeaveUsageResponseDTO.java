package payroll.leaveUsage.dto;

import lombok.Getter;
import payroll.entity.LeaveUsage;

import java.time.LocalDate;

@Getter
public class LeaveUsageResponseDTO {
    private Long id;
    private String empCode;
    private String empName;
    private LocalDate startDate;
    private LocalDate endDate;
    private double usedDays;
    private String leaveType;

    public LeaveUsageResponseDTO(LeaveUsage leaveUsage, String empCode, String empName, String leaveType){
        this.id = leaveUsage.getId();
        this.empCode = empCode;
        this.empName = empName;
        this.startDate = leaveUsage.getStartDate();
        this.endDate = leaveUsage.getEndDate();
        this.usedDays = leaveUsage.getUsedDays();
        this.leaveType = leaveUsage.getLeaveType();
    }
}
