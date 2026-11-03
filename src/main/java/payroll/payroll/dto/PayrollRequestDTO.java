package payroll.payroll.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class PayrollRequestDTO {
    @NotBlank(message = "사원코드는 필수 입력값입니다.")
    private String empCode;
    @NotNull(message = "급여 산정 연도는 필수 입력값입니다.")
    @Max(value = 3000, message = "다시 입력해주세요.")
    @Min(value = 1000, message = "다시 입력해주세요.")
    private int year;
    @NotNull(message = "급여 산정 월은 필수 입력값입니다.")
    @Max(value = 12, message = "다시 입력해주세요.")
    @Min(value = 1, message = "다시 입력해주세요.")
    private int month;
    private int retroactivePay;
    @Min(value = 0, message = "미지급수당은 음수가 될 수 없습니다.")
    private int unpaidAllowance;
    private String unpaidAllowanceReason;
    @Min(value = 0, message = "식대는 음수가 될 수 없습니다.")
    private int dailyMealAllowance;
    @Min(value = 0, message = "기타수당은 음수가 될 수 없습니다.")
    private int otherAllowance;

    @NotNull(message = "평균급여는 필수 입력값입니다.")
    @Min(value = 0, message = "평균급여는 음수가 될 수 없습니다.")
    private int averagePayroll;
    @Min(value = 0, message = "건강보험정산값은 음수가 될 수 없습니다.")
    private int healthInsuranceAdjustment;
    @Min(value = 0, message = "퇴직정산소득세는 음수가 될 수 없습니다.")
    private int retirementIncomeTax;
    @Min(value = 0, message = "퇴직정산주민세는 음수가 될 수 없습니다.")
    private int retirementLocalIncomeTax;
    @Min(value = 0, message = "소득세는 음수가 될 수 없습니다.")
    private int incomeTax;
}
