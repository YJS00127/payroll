package payroll.payroll.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class PayrollHistoryDeductionDTO {
    private int nationalPension;
    private int healthInsurance;
    private int longTermCareInsurance;
    private int employmentInsurance;
    private int healthInsuranceAdjustment;
    private int longTermCareAdjustment;
    private int retirementIncomeTax;
    private int retirementLocalIncomeTax;
    private int incomeTax;
    private int localIncomeTax;

    public int totalDeduction(){
        return nationalPension +
                healthInsurance +
                longTermCareInsurance +
                employmentInsurance +
                healthInsuranceAdjustment +
                longTermCareAdjustment +
                retirementIncomeTax +
                retirementLocalIncomeTax +
                incomeTax +
                localIncomeTax;
    }
}
