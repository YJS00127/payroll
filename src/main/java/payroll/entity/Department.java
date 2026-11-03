package payroll.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.annotations.Comment;
import payroll.department.dto.DeptRequestDTO;

import java.time.LocalDate;

@Getter
@Entity
@Table(name="departments")
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "department_seq")
    @SequenceGenerator(name = "department_seq", sequenceName = "department_seq", allocationSize = 1)
    private Long id;

    @Comment("부서 이름")
    @Column(name="name")
    private String name;

    @Comment("부서 코드")
    @Column(name="code")
    private String code;

    @Comment("부서 존재 여부")
    @Column(name="delete_yn")
    private String deleteYn;

    @Comment("부서 등록일")
    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDate createdAt;

    @Comment("부서 생성일")
    @Column(name="created_dept_date")
    private LocalDate createdDeptDate;

    @Builder
    public Department(String name, String code, LocalDate createdDeptDate){
        this.name = name;
        this.code = code;
        this.createdDeptDate = createdDeptDate;
    }

//    부서 정보 수정
    public void update(DeptRequestDTO request){
        this.name = request.getName();
        this.code = request.getCode();
        this.createdDeptDate = request.getCreatedDeptDate();
    }

    public void delete(){
        this.deleteYn = "Y";
    }
}
