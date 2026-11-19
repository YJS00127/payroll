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
    private int usedDays;

    public LeaveUsageResponseDTO(LeaveUsage leaveUsage, String empCode, String empName){
        this.id = leaveUsage.getId();
        this.empCode = empCode;
        this.empName = empName;
        this.startDate = leaveUsage.getStartDate();
        this.endDate = leaveUsage.getEndDate();
        this.usedDays = leaveUsage.getUsedDays();
    }
}
