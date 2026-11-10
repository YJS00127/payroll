package payroll.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.annotations.Comment;
import payroll.salaryHistory.dto.SalaryHistoryRequestDTO;

import java.time.LocalDate;

@Getter
@Entity
@Builder
@Table(name = "salary_history")
public class SalaryHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "salary_history_seq")
    @SequenceGenerator(name = "salary_history_seq", sequenceName = "salary_history_seq", allocationSize = 1)
    private Long id;

    @Column(name = "emp_id")
    private Long empId;

    @Comment("변경 전 시급")
    @Column(name = "previous_hourly_wage")
    private int previousHourlyWage;

    @Comment("변경 시급")
    @Column(name = "new_hourly_wage")
    private int newHourlyWage;

    @Comment("변경된 급여 적용일")
    @Column(name = "effective_date")
    private LocalDate effectiveDate;

    @Comment("급여 변경 사유")
    @Column(name = "reason")
    private String reason;

    public void update(SalaryHistoryRequestDTO requestDTO){
        this.previousHourlyWage = requestDTO.getPreviousHourlyWage();
        this.newHourlyWage = requestDTO.getNewHourlyWage();
        this.effectiveDate = requestDTO.getEffectiveDate();
        this.reason = requestDTO.getReason();
    }
}
