package payroll.leaveUsage.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LeaveUsageSearchDTO {
    private String keyword;
    private Integer year;
    private Integer month;
}
