package payroll.leave.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import payroll.entity.LeaveRecord;

@Getter
public class LeaveRequestDTO {
    @NotBlank(message = "사원 코드를 입력해주세요")
    private String empCode;
    @NotNull(message = "월차 발생 연도를 입력해주세요")
    @Max(value = 2100, message = "기준 연도를 다시 입력해주세요")
    @Min(value = 1900, message = "기준 연도를 다시 입력해주세요")
    private int year;
    @NotNull(message = "월차 발생 월을 입력해주세요")
    @Max(value = 12, message = "기준 월을 다시 입력해주세요")
    @Min(value = 1, message = "기준 월을 다시 입력해주세요")
    private int month;
    private String deleteYn;
    @NotNull(message = "발생 월차를 입력해주세요")
    @Max(value = 70, message = "발생 월차를 다시 입력해주세요")
    @Min(value = 0, message = "발생 월차는 음수가 될 수 없습니다")
    private int accruedLeave;

    public LeaveRecord toEntity(Long empId, double previousTotalLeave){
        return LeaveRecord.builder()
                .empId(empId)
                .year(this.year)
                .month(this.month)
                .accruedLeave(this.accruedLeave)
                .totalLeave(previousTotalLeave + this.accruedLeave)
                .build();
    }
}
