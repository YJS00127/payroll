package payroll.payslip.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PayslipSearchDTO {
    private String keyword;
    private String deptName;
    private String empRank;
    private Integer year;
    private Integer month;
}
