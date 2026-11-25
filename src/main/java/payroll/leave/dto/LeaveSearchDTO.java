package payroll.leave.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LeaveSearchDTO {
    private String keyword;
    private Integer year;
    private Integer month;
}
