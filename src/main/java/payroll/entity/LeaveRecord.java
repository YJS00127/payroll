package payroll.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.Comment;

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

    @Comment("총 연차")
    @Column(name = "total_leave")
    private int totalLeave;

    @Comment("사용 연차")
    @Column(name = "used_leave")
    private int usedLeave;

    @Comment("남은 연차")
    @Column(name = "remaining_leave")
    private int remainingLeave;

}
