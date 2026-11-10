package payroll.insuranceRate.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import payroll.entity.InsuranceRateHistory;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
public class InsuranceRateRequestDTO {
    @NotNull(message = "국민연금 공제율은 필수 입력값입니다.")
    @DecimalMax(value = "100.0", message = "국민연금 공제율은 100 미만이어야 합니다.")
    @Min(value = 0, message = "국민연금 공제율은 0 이상이어야 합니다.")
    private BigDecimal nationalPensionRate;

    @NotNull(message = "건강보험 공제율은 필수 입력값입니다.")
    @DecimalMax(value = "100.0", message = "건강보험 공제율은 100 미만이어야 합니다.")
    @Min(value = 0, message = "건강보험 공제율은 0 이상이어야 합니다.")
    private BigDecimal healthInsuranceRate;

    @NotNull(message = "노인장기요양 공제율은 필수 입력값입니다.")
    @DecimalMax(value = "100.0", message = "노인장기요양 공제율은 100 미만이어야 합니다.")
    @Min(value = 0, message = "노인장기요양 공제율은 0 이상이어야 합니다.")
    private BigDecimal longTermCareInsuranceRate;

    @NotNull(message = "고용보험 공제율은 필수 입력값입니다.")
    @DecimalMax(value = "100.0", message = "고용보험 공제율은 100 미만이어야 합니다.")
    @Min(value = 0, message = "고용보험 공제율은 0 이상이어야 합니다.")
    private BigDecimal employmentInsuranceRate;

    @NotNull(message = "주민세 공제율은 필수 입력값입니다.")
    @DecimalMax(value = "100.0", message = "주민세 공제율은 100 미만이어야 합니다.")
    @Min(value = 0, message = "주민세 공제율은 0 이상이어야 합니다..")
    private BigDecimal localIncomeTaxRate;

    @NotNull(message = "공제율 변경 적용일은 필수 입력값입니다.")
    @Max(value = 3000, message = "다시 입력해주세요.")
    @Min(value = 1000, message = "다시 입력해주세요.")
    private int year;
    @NotNull(message = "공제율 변경 적용일은 필수 입력값입니다.")
    @Max(value = 12, message = "다시 입력해주세요.")
    @Min(value = 1, message = "다시 입력해주세요.")
    private int month;

    public InsuranceRateHistory toEntity(){
        return InsuranceRateHistory.builder()
                .nationalPensionRate(this.nationalPensionRate)
                .healthInsuranceRate(this.healthInsuranceRate)
                .longTermCareInsuranceRate(this.longTermCareInsuranceRate)
                .employmentInsuranceRate(this.employmentInsuranceRate)
                .localIncomeTaxRate(this.localIncomeTaxRate)
                .effectiveDate(LocalDate.of(this.year, this.month, 1))
                .build();
    }
}
