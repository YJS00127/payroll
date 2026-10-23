package payroll.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.Comment;

import java.time.LocalDate;

@Entity
@Table(name = "salary_histories")
public class SalaryHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "salary_history_seq")
    @SequenceGenerator(name = "salary_history_seq", sequenceName = "salary_history_seq", allocationSize = 1)
    private Long id;

    @Column(name = "emp_id")
    private Long empId;

    @Comment("이전 기본 급여")
    @Column(name = "previous_basic_salary")
    private int previousBasicSalary;

    @Comment("이전 총 급여")
    @Column(name = "previous_gross_salary")
    private int previousGrossSALARY;

    @Comment("변경된 기본 급여")
    @Column(name = "new_basic_salary")
    private int newBasicSalary;

    @Comment("변경된 총 급여")
    @Column(name = "new_gross_salary")
    private int newGrossSalary;

    @Comment("변경된 실수령액")
    @Column(name = "new_net_salary")
    private int newNetSalary;

    @Comment("변경된 급여 적용일")
    @Column(name = "effective_date")
    private LocalDate effectiveDate;

    @Comment("급여 변경 사유")
    @Column(name = "reason")
    private String reason;
}
