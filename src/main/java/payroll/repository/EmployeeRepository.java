package payroll.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import payroll.entity.Employee;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface EmployeeRepository extends JpaRepository<Employee, Long>, JpaSpecificationExecutor<Employee> {
    Optional<Employee> findByCode(@Param("code") String code);

    @Query("SELECT e.id FROM Employee e WHERE e.code = :code")
    Optional<Long> findIdByCode(@Param("code") String code);

    List<Employee> findByCodeIn(Set<String> codes);

    boolean existsByCode(@Param("code") String code);

    boolean existsByCodeIn(@Param("code") Set<String> codes);

    boolean existsByDeptIdAndDeleteYn(@Param("deptId")Long deptId, @Param("deleteYn")String deleteYn);

    boolean existsByDeptIdInAndDeleteYn(@Param("deptId")List<Long> deptIds, @Param("deleteYn")String deleteYn);
}
