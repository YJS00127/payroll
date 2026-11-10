package payroll.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.query.Param;
import payroll.entity.Employee;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface EmployeeRepository extends JpaRepository<Employee, Long>, JpaSpecificationExecutor<Employee> {
    Optional<Employee> findByCode(@Param("code") String code);

    Optional<Long> findIdByCode(@Param("code") String code);

    List<Employee> findByCodeIn(Set<String> codes);

    boolean existsByCode(@Param("code") String code);

    boolean existsByCodeIn(@Param("code") Set<String> codes);
}
