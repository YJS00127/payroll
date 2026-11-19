package payroll.workHistory.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WorkSearchDTO {
    private String keyword;
    private Integer year;
    private Integer month;
}
