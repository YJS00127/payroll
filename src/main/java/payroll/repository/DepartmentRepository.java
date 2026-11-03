package payroll.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.query.Param;
import payroll.entity.Department;

public interface DepartmentRepository extends JpaRepository<Department, Long>, JpaSpecificationExecutor<Department> {
    String findNameById(@Param("id") Long id);

    Long findIdByCode(@Param("code") String code);

    boolean existsByCode(@Param("code") String code);
}
