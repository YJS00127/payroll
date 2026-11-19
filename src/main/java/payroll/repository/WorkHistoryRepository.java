package payroll.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.query.Param;
import payroll.entity.WorkHistory;

import java.util.Optional;

public interface WorkHistoryRepository extends JpaRepository<WorkHistory, Long>, JpaSpecificationExecutor<WorkHistory> {
    Optional<WorkHistory> findByEmpIdAndYearAndMonth(@Param("empId") Long empId, @Param("year") int year, @Param("month") int month);

    boolean existsByEmpIdAndYearAndMonth(@Param("empId") Long empId, @Param("year") int year, @Param("month") int month);
}
