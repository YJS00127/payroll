package payroll.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.query.Param;
import payroll.entity.Employee;

import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long>, JpaSpecificationExecutor<Employee> {
//    Long findDeptIdByDeptCode(@Param("deptCode") String DeptCode);

    Optional<Employee> findByCode(@Param("code") String code);

    Long findIdByCode(@Param("code") String code);

    boolean existsByCode(@Param("code") String code);
}
