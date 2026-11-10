package payroll.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.annotations.Comment;

import java.time.LocalDate;

@Getter
@Entity
@Table(name = "leave_usage")
@Builder
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

    @Comment("휴가 마지막일")
    @Column(name = "end_date", comment = "휴가 마지막일")
    private LocalDate endDate;

    @Comment("휴가 사용일 수")
    @Column(name = "used_days", comment = "휴가 사용일 수")
    private int usedDays;

    @Comment("row 삭제 여부")
    @Column(name = "delete_yn", comment = "row 삭제 여부")
    private String deleteYn;

    public void update(LocalDate startDate, LocalDate endDate, int usedDays){
        this.startDate = startDate;
        this.endDate = endDate;
        this.usedDays = usedDays;
    }

    public void delete(){
        this.deleteYn = "Y";
    }
}
