package payroll.leave.dto;

import lombok.Getter;
import payroll.entity.LeaveRecord;

@Getter
public class LeaveResponseDTO {
    private Long id;
    private String empCode;
    private String empName;
    private int year;
    private int month;
    private int totalLeave;
    private int usedLeave;
    private int remainingLeave;
    private int accruedLeave;

    public LeaveResponseDTO(LeaveRecord leaveRecord,
                            String empCode, String empName){
        this.id = leaveRecord.getId();
        this.empCode = empCode;
        this.empName = empName;
        this.year = leaveRecord.getYear();
        this.month = leaveRecord.getMonth();
        this.totalLeave = leaveRecord.getTotalLeave();
        this.usedLeave = leaveRecord.getUsedLeave();
        this.remainingLeave = leaveRecord.getRemainingLeave();
        this.accruedLeave = leaveRecord.getAccruedLeave();
    }
}
