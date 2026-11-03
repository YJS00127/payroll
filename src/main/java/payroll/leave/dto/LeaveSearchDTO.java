package payroll.leave.dto;

import lombok.Getter;

@Getter
public class LeaveSearchDTO {
    private String empCode;
    private Integer year;
    private Integer month;
    private Integer remainingLeave;
}
