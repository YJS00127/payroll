package payroll.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.annotations.Comment;
import payroll.insuranceRate.dto.InsuranceRateRequestDTO;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Entity
@Table(name = "insurance_rate")
public class InsuranceRateHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "insurance_rate_seq")
    @SequenceGenerator(name = "insurance_rate_seq", sequenceName = "insurance_rate_seq", allocationSize = 1)
    private Long id;
    @Comment("국민연금 공제율")
    @Column(name = "national_pension_rate")
    private BigDecimal nationalPensionRate;
    @Comment("건강보험 공제율")
    @Column(name = "health_insurance_rate")
    private BigDecimal healthInsuranceRate;
    @Comment("노인장기요양 공제율")
    @Column(name = "long_term_care_insurance_rate")
    private BigDecimal longTermCareInsuranceRate;
    @Comment("고용보험 공제율")
    @Column(name = "employment_insurance_rate")
    private BigDecimal employmentInsuranceRate;
    @Comment("주민세 공제율")
    @Column(name = "local_income_tax_rate")
    private BigDecimal localIncomeTaxRate;
    @Comment("공제율 적용일")
    @Column(name = "effective_date")
    private LocalDate effectiveDate;


    @Builder
    public InsuranceRateHistory(BigDecimal nationalPensionRate, BigDecimal healthInsuranceRate, BigDecimal longTermCareInsuranceRate,
                                BigDecimal employmentInsuranceRate, BigDecimal localIncomeTaxRate, LocalDate effectiveDate){
        this.nationalPensionRate = nationalPensionRate;
        this.healthInsuranceRate = healthInsuranceRate;
        this.longTermCareInsuranceRate = longTermCareInsuranceRate;
        this.employmentInsuranceRate = employmentInsuranceRate;
        this.localIncomeTaxRate = localIncomeTaxRate;
        this.effectiveDate = effectiveDate;
    }

    public void update(InsuranceRateRequestDTO requestDTO){
        this.nationalPensionRate = requestDTO.getNationalPensionRate();
        this.healthInsuranceRate = requestDTO.getHealthInsuranceRate();
        this.longTermCareInsuranceRate = requestDTO.getLongTermCareInsuranceRate();
        this.employmentInsuranceRate = requestDTO.getEmploymentInsuranceRate();
        this.localIncomeTaxRate = requestDTO.getLocalIncomeTaxRate();
        this.effectiveDate = LocalDate.of(requestDTO.getYear(), requestDTO.getMonth(), 1);
    }

}
