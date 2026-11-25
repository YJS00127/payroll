package payroll.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import payroll.employee.dto.EmpUpdateRequestDTO;

import java.time.LocalDate;

@Getter
@Entity
@Table(name="employees")
@NoArgsConstructor
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "employee_seq")
    @SequenceGenerator(name = "employee_seq", sequenceName = "employee_seq", allocationSize = 1)
    private Long id;

    @Comment("부서 Id")
    @Column(name = "dept_id")
    private Long deptId;

    @Comment("사원 이름")
    @Column(name = "name")
    private String name;

    @Comment("사원 코드")
    @Column(name = "code")
    private String code;

    @Comment("생년월일")
    @Column(name = "birth")
    private LocalDate birth;

    @Comment("성별")
    @Column(name = "gender")
    private String gender;

    @Comment("직급")
    @Column(name = "rank")
    private String rank;

    @Comment("입사일")
    @Column(name = "joining_date")
    private LocalDate joiningDate;

    @Comment("재직 여부")
    @Column(name = "delete_yn", insertable = false)
    private String deleteYn;

    @Comment("등록일")
    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDate createdAt;

    @Comment("시급")
    @Column(name = "hourly_wage")
    private int hourlyWage;

    @Comment("퇴사일")
    @Column(name = "retirement_date")
    private LocalDate retirementDate;

    @Comment("소정근로시간")
    @Column(name = "contract_daily_work_minutes")
    private int contractDailyWorkMinutes;

    @Comment("월평균 급여")
    @Column(name = "initial_monthly_income")
    private int initialMonthlyIncome;

    @Builder
    public Employee(Long deptId, String name, String code, LocalDate birth, String gender, String rank, LocalDate joiningDate,
                    LocalDate retirementDate, int hourlyWage, int contractDailyWorkMinutes,int initialMonthlyIncome){
        this.deptId = deptId;
        this.name = name;
        this.code = code;
        this.birth = birth;
        this.gender = gender;
        this.rank = rank;
        this.joiningDate = joiningDate;
        this.hourlyWage = hourlyWage;
        this.retirementDate = retirementDate;
        this.contractDailyWorkMinutes = contractDailyWorkMinutes;
        this.initialMonthlyIncome = initialMonthlyIncome;
    }

    public void update(EmpUpdateRequestDTO request, Long deptId){
        this.deptId = deptId;
        this.name = request.getName();
        this.code = request.getCode();
        this.birth = request.getBirth();
        this.gender = request.getGender();
        this.rank = request.getRank();
        this.joiningDate = request.getJoiningDate();
        this.retirementDate = request.getRetirementDate();
        this.contractDailyWorkMinutes = (int)(request.getContractDailyWorkHour() * 60);
        this.initialMonthlyIncome = request.getInitialMonthlyIncome();
    }

    public void updateHourlyWage(int hourlyWage){
        this.hourlyWage = hourlyWage;
    }

    public void delete(){
        this.deleteYn = "Y";
    }
}
