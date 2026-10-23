package payroll.entity;


import jakarta.persistence.*;
import org.hibernate.annotations.Comment;

import java.time.LocalDate;

@Entity
@Table(name="employees")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "employee_seq")
    @SequenceGenerator(name = "employee_seq", sequenceName = "employee_seq", allocationSize = 1)
    private Long id;

    @Comment("부서 ID")
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

    @Comment("직급")
    @Column(name = "rank")
    private String rank;

    @Comment("이메일")
    @Column(name = "email")
    private String email;

    @Comment("주소")
    @Column(name = "address")
    private String address;

    @Comment("입사일")
    @Column(name = "joining_date")
    private LocalDate joiningDate;

    @Comment("재직 여부")
    @Column(name = "delete_yn")
    private String deleteYn;

    @Comment("등록일")
    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDate createdAt;
}
