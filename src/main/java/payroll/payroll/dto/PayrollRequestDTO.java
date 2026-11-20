package payroll.payroll.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class PayrollRequestDTO {
    @NotBlank(message = "사원코드를 입력해주세요")
    private String empCode;
    @NotNull(message = "급여 산정 연도를 입력해주세요")
    @Max(value = 2100, message = "기준연도를 다시 입력해주세요.")
    @Min(value = 1900, message = "기준연도를 다시 입력해주세요.")
    private int year;
    @NotNull(message = "급여 산정 월을 입력해주세요.")
    @Max(value = 12, message = "기준월을 다시 입력해주세요.")
    @Min(value = 1, message = "기준월을 다시 입력해주세요.")
    private int month;
    private int retroactivePay;
    @Min(value = 0, message = "미지급수당은 음수가 될 수 없습니다.")
    private int unpaidAllowance;
    private String unpaidAllowanceReason;
    @Min(value = 0, message = "식대는 음수가 될 수 없습니다.")
    private int dailyMealAllowance;
    @Min(value = 0, message = "기타수당은 음수가 될 수 없습니다.")
    private int otherAllowance;


    @Min(value = 0, message = "건강보험정산값은 음수가 될 수 없습니다.")
    private int healthInsuranceAdjustment;
    @Min(value = 0, message = "퇴직정산소득세는 음수가 될 수 없습니다.")
    private int retirementIncomeTax;
    @Min(value = 0, message = "퇴직정산주민세는 음수가 될 수 없습니다.")
    private int retirementLocalIncomeTax;
    @Min(value = 0, message = "소득세는 음수가 될 수 없습니다.")
    private int incomeTax;
}
