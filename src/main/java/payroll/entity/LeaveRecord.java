package payroll.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import payroll.leave.dto.LeaveRequestDTO;
import payroll.leave.dto.LeaveUpdateRequestDTO;

@Getter
@Entity
@Table(name = "leave_record")
@NoArgsConstructor
public class LeaveRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "leave_record_seq")
    @SequenceGenerator(name = "leave_record_seq", sequenceName = "leave_record_seq", allocationSize = 1)
    private Long id;

    @Column(name = "emp_id")
    private Long empId;

    @Column(name = "year")
    private int year;

    @Column(name = "month")
    private int month;

    @Comment("총 월차")
    @Column(name = "total_leave")
    private double totalLeave;

    @Comment("사용 월차")
    @Column(name = "used_leave")
    private double usedLeave;

    @Comment("남은 월차")
    @Column(name = "remaining_leave")
    private double remainingLeave;

    @Comment("삭제 여부")
    @Column(name = "delete_yn", insertable = false)
    private String deleteYn;

    @Comment("발생 월차")
    @Column(name = "accrued_leave")
    private int accruedLeave;

    @Builder
    public LeaveRecord(Long empId, int year, int month, double totalLeave,
                       double usedLeave, double remainingLeave, int accruedLeave){
        this.empId = empId;
        this.year = year;
        this.month = month;
        this.totalLeave = totalLeave;
        this.usedLeave = usedLeave;
        this.remainingLeave = remainingLeave;
        this.accruedLeave = accruedLeave;
    }

    public void update(LeaveUpdateRequestDTO requestDTO){
        // 발생 휴가 차이 생길 시에만 총 휴가 변경
        this.totalLeave = calcTotalLeave(requestDTO);
        this.year = requestDTO.getYear();
        this.month = requestDTO.getMonth();
        this.usedLeave = requestDTO.getUsedLeave();
        this.accruedLeave = requestDTO.getAccruedLeave();
        this.remainingLeave = this.totalLeave - this.usedLeave;
    }

    private int calcTotalLeave(LeaveUpdateRequestDTO requestDTO){
        int totalLeave = requestDTO.getTotalLeave();
        if(requestDTO.getTotalLeave() - this.totalLeave != requestDTO.getAccruedLeave() - this.accruedLeave&&
                this.accruedLeave != requestDTO.getAccruedLeave()){
            totalLeave = requestDTO.getTotalLeave() + (requestDTO.getAccruedLeave() - this.accruedLeave);
        }
        return totalLeave;
    }

    public void delete(){
        this.deleteYn = "Y";
    }

    public void useLeave(double usedLeave){
        this.usedLeave += usedLeave;
        this.remainingLeave = this.remainingLeave - usedLeave;
    }

    public void updateLeave(double previousUsedDays, double newUsedDays){
        this.usedLeave = this.usedLeave + previousUsedDays - newUsedDays;
        this.remainingLeave = this.remainingLeave - previousUsedDays + newUsedDays;
    }

    public void cancelLeave(double leaveDays){
        this.usedLeave -= leaveDays;
        this.remainingLeave += leaveDays;
    }
}
