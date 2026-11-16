package payroll.attendance.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AttendanceSearchDTO {
    @NotBlank(message = "사원명 또는 사원코드를 입력해주세요")
    private String keyword;
    @Max(value=2100, message="다시 입력해주세요")
    @Min(value=1900, message="다시 입력해주세요")
    private Integer year;
    @Max(value=12, message="다시 입력해주세요")
    @Min(value=1, message="다시 입력해주세요")
    private Integer month;
    @Max(value=31, message="다시 입력해주세요")
    @Min(value=1, message="다시 입력해주세요")
    private Integer day;
}
