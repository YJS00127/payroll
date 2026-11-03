package payroll.leave.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import payroll.entity.LeaveRecord;

@Getter
public class LeaveRequestDTO {
    @NotBlank(message = "사원 코드는 필수 입력값입니다.")
    private String empCode;
    @NotBlank(message = "월차 발생 연도는 필수 입력값입니다.")
    private int year;
    @NotBlank(message = "월차 발생 월은 필수 입력값입니다.")
    private int month;
    @NotBlank(message = "총 월차는 필수 입력값입니다.")
    private int totalLeave;
//    @NotBlank
//    private int usedLeave;

    public LeaveRecord toEntity(Long empId){
        return LeaveRecord.builder()
                .empId(empId)
                .year(this.year)
                .month(this.month)
                .build();
    }
}
