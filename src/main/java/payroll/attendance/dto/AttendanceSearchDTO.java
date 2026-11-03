package payroll.attendance.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class AttendanceSearchDTO {
    @NotBlank
    private String empCode;
    private Integer year;
    private Integer month;
    private Integer day;
}
