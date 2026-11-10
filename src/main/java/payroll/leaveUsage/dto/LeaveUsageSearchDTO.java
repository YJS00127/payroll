package payroll.leaveUsage.dto;

import lombok.Getter;

@Getter
public class LeaveUsageSearchDTO {
    private String empCode;
    private Integer year;
    private Integer month;
}
