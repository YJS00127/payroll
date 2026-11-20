package payroll.leave.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class LeaveUpdateRequestDTO {
    @NotBlank(message = "사원 정보가 비어있습니다")
    private String empCode;
    @Max(value=50, message="총 휴가를 다시 입력해주세요")
    @Min(value=0, message="총 휴가는 음수가 될 수 없습니다")
    private int totalLeave;
    @Max(value=50, message="사용 휴가를 다시 입력해주세요")
    @Min(value=0, message="사용 휴가는 음수가 될 수 없습니다")
    private int usedLeave;
    @Max(value=50, message="발생 휴가를 다시 입력해주세요")
    @Min(value=0, message="발생 휴가는 음수가 될 수 없습니다")
    private int accruedLeave;
    @NotNull(message="기준 연도를 입력해주세요")
    @Max(value=2100, message="기준 연도를 다시 입력해주세요")
    @Min(value=1900, message="기준 연도를 다시 입력해주세요")
    private int year;
    @NotNull(message="기준 월을 입력해주세요")
    @Max(value=12, message="기준 월을 다시 입력해주세요")
    @Min(value=1, message="기준 월을 다시 입력해주세요")
    private int month;
}
