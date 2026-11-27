package payroll.leaveUsage.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.Getter;
import lombok.Setter;
import payroll.entity.LeaveUsage;

import java.time.LocalDate;

@Getter
@Setter
public class LeaveUsageRequestDTO {
    @NotBlank(message = "사원코드는 필수값입니다")
    private String empCode;
//    @Past(message = "과거 날짜에 휴가를 등록할 수 없습니다")
    @NotNull(message = "휴가 시작일은 필수입니다")
    private LocalDate startDate;
//    @Past(message = "과거 날짜에 휴가를 등록할 수 없습니다")
    @NotNull(message = "휴가 종료일은 필수입니다")
    private LocalDate endDate;
    @NotBlank(message = "휴가 종류는 필수입니다")
    private String leaveType;

    public LeaveUsage toEntity(Long empId, double usedDays){
        return LeaveUsage.builder()
                .empId(empId)
                .startDate(this.startDate)
                .endDate(this.endDate)
                .usedDays(usedDays)
                .leaveType(this.leaveType)
                .build();
    }
}
