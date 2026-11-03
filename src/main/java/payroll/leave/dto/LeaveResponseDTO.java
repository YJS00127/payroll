package payroll.leave.dto;

import payroll.entity.LeaveRecord;

public class LeaveResponseDTO {
    private String empCode;
    private String empName;
    private int year;
    private int totalLeave;
    private int usedLeave;
    private int remainingLeave;

    public LeaveResponseDTO(LeaveRecord leaveRecord,
                            String empCode, String empName){
        this.empCode = empCode;
        this.empName = empName;
        this.year = leaveRecord.getYear();
        this.totalLeave = leaveRecord.getTotalLeave();
        this.usedLeave = leaveRecord.getUsedLeave();
        this.remainingLeave = leaveRecord.getRemainingLeave();
    }
}
