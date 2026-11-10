package payroll.leaveUsage.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import payroll.entity.LeaveUsage;

import java.time.LocalDate;

@Getter
public class LeaveUsageRequestDTO {
    @NotBlank(message = "사원코드는 필수값입니다.")
    private String empCode;
    @NotNull(message = "휴가 시작일은 필수입니다.")
    private LocalDate startDate;
    @NotNull(message = "휴가 마지막일은 필수입니다.")
    private LocalDate endDate;

    public LeaveUsage toEntity(Long empId, int usedDays){
        return LeaveUsage.builder()
                .empId(empId)
                .startDate(this.startDate)
                .endDate(this.endDate)
                .usedDays(usedDays)
                .build();
    }
}
