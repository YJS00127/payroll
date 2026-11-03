package payroll.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.annotations.Comment;
import payroll.leave.dto.LeaveRequestDTO;

@Getter
@Entity
@Table(name = "leave_records")
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
    private int totalLeave;

    @Comment("사용 월차")
    @Column(name = "used_leave")
    private int usedLeave;

    @Comment("남은 월차")
    @Column(name = "remaining_leave")
    private int remainingLeave;

    @Comment("삭제 여부")
    @Column(name = "delete_yn")
    private String deleteYn;

    @Builder
    public LeaveRecord(Long empId, int year, int month){
        this.empId = empId;
        this.year = year;
        this.month = month;
    }

    public void update(LeaveRequestDTO requestDTO){
        this.totalLeave = requestDTO.getTotalLeave();
//        this.usedLeave = requestDTO.getUsedLeave();
        this.remainingLeave = this.totalLeave - this.usedLeave;
    }

    public void delete(){
        this.deleteYn = "Y";
    }
}
