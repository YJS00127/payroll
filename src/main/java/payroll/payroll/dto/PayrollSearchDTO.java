package payroll.payroll.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PayrollSearchDTO {
    private String keyword;
    private Integer year;
    private Integer month;
}
