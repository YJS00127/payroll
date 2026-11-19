package payroll.payroll.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PayrollSearchDTO {
    private String empCode;
    private Integer year;
    private Integer month;
}
