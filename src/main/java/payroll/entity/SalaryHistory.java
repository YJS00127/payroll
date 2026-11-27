package payroll.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import payroll.salaryHistory.dto.SalaryHistoryRequestDTO;

import java.time.LocalDate;

@Getter
@Entity
@Table(name = "salary_history")
@NoArgsConstructor
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

    @Comment("급여 수정일")
    @Column(name = "update_at", insertable = false, updatable = false)
    private LocalDate updateAt;

    @Comment("삭제 여부")
    @Column(name = "delete_yn", insertable = false)
    private String deleteYn;

    @Builder
    public SalaryHistory(Long empId, int previousHourlyWage, int newHourlyWage, LocalDate effectiveDate, String reason){
        this.empId = empId;
        this.previousHourlyWage = previousHourlyWage;
        this.newHourlyWage = newHourlyWage;
        this.effectiveDate = effectiveDate;
        this.reason = reason;
    }

    public void update(SalaryHistoryRequestDTO requestDTO){
        this.previousHourlyWage = requestDTO.getPreviousHourlyWage();
        this.newHourlyWage = requestDTO.getNewHourlyWage();
        this.effectiveDate = requestDTO.getEffectiveDate();
        this.reason = requestDTO.getReason();
    }

    public void delete(){
        this.deleteYn = "Y";
    }
}
