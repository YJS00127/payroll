package payroll.insuranceRate.dto;

import lombok.Getter;
import payroll.entity.InsuranceRateHistory;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
public class InsuranceRateResponseDTO {
    private Long id;
    private BigDecimal nationalPensionRate;
    private BigDecimal healthInsuranceRate;
    private BigDecimal longTermCareInsuranceRate;
    private BigDecimal employmentInsuranceRate;
    private BigDecimal localIncomeTaxRate;
    private LocalDate effectiveDate;

    public InsuranceRateResponseDTO(InsuranceRateHistory insuranceRateHistory){
        this.id = insuranceRateHistory.getId();
        this.nationalPensionRate = insuranceRateHistory.getNationalPensionRate();
        this.healthInsuranceRate = insuranceRateHistory.getHealthInsuranceRate();
        this.longTermCareInsuranceRate = insuranceRateHistory.getLongTermCareInsuranceRate();
        this.employmentInsuranceRate = insuranceRateHistory.getEmploymentInsuranceRate();
        this.localIncomeTaxRate = insuranceRateHistory.getLocalIncomeTaxRate();
        this.effectiveDate = insuranceRateHistory.getEffectiveDate();
    }
}
