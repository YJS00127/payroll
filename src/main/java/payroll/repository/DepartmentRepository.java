package payroll.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import payroll.entity.Department;

import java.util.*;

public interface DepartmentRepository extends JpaRepository<Department, Long>, JpaSpecificationExecutor<Department> {
    @Query("SELECT d.name FROM Department d WHERE d.id = :id")
    String findNameById(@Param("id") Long id);

    boolean existsByCode(@Param("code") String code);

    @Query("SELECT d.id FROM Department d WHERE d.name = :name")
    Optional<Long> findIdByName(@Param("name")String name);

    @Query("SELECT d.id FROM Department d WHERE d.code = :keyword OR d.name = :keyword")
    Optional<Long> findIdByCodeOrName(@Param("keyword") String keyword);
}
