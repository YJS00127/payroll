package payroll.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import java.time.LocalDate;

@Getter
@Entity
@Table(name = "leave_usage")
@NoArgsConstructor
public class LeaveUsage {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "leave_usage_seq")
    @SequenceGenerator(name = "leave_usage_seq", sequenceName = "leave_usage_seq", allocationSize = 1)
    private Long id;

    @Column(name = "emp_id")
    private Long empId;

    @Comment("휴가 시작일")
    @Column(name = "start_date")
    private LocalDate startDate;

    @Comment("휴가 종료일")
    @Column(name = "end_date")
    private LocalDate endDate;

    @Comment("휴가 사용일 수")
    @Column(name = "used_days")
    private double usedDays;

    @Comment("삭제 여부")
    @Column(name = "delete_yn", insertable = false)
    private String deleteYn;

    @Comment("휴가 종류")
    @Column(name = "leave_type")
    private String leaveType;

    @Builder
    public LeaveUsage(Long empId, LocalDate startDate, LocalDate endDate, double usedDays, String leaveType){
        this.empId = empId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.usedDays = usedDays;
        this.leaveType = leaveType;
    }

    public void update(LocalDate startDate, LocalDate endDate, double usedDays, String leaveType){
        this.startDate = startDate;
        this.endDate = endDate;
        this.usedDays = usedDays;
        this.leaveType = leaveType;
    }

    public void delete(){
        this.deleteYn = "Y";
    }
}
