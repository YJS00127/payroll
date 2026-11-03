package payroll.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.annotations.Comment;
import payroll.employee.dto.EmpRequestDTO;
import payroll.employee.dto.EmpUpdateRequestDTO;

import java.time.LocalDate;

@Getter
@Entity
@Table(name="employees")
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
    @Column(name = "delete_yn")
    private String deleteYn;

    @Comment("등록일")
    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDate createdAt;

    @Comment("시급")
    @Column(name = "hourly_wage")
    private int hourlyWage;

    @Builder
    public Employee(Long deptId, String name, String code, LocalDate birth, String gender,
                    String rank, LocalDate joiningDate, int hourlyWage){
        this.deptId = deptId;
        this.name = name;
        this.code = code;
        this.birth = birth;
        this.gender = gender;
        this.rank = rank;
        this.joiningDate = joiningDate;
        this.hourlyWage = hourlyWage;
    }

    public void update(EmpUpdateRequestDTO request, Long deptId){
        this.deptId = deptId;
        this.name = request.getName();
        this.code = request.getCode();
        this.birth = request.getBirth();
        this.gender = request.getCode();
        this.rank = request.getRank();
        this.joiningDate = request.getJoiningDate();
        this.hourlyWage = request.getHourlyWage();
    }

    public void delete(){
        this.deleteYn = "Y";
    }
}
