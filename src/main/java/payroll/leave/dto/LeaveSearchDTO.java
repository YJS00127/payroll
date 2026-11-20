package payroll.leave.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LeaveSearchDTO {
    private String empCode;
    private Integer year;
    private Integer month;
    private Integer remainingLeave;
}
