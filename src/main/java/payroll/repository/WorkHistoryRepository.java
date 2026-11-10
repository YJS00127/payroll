package payroll.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.query.Param;
import payroll.entity.WorkHistory;
import payroll.workHistory.dto.WorkResponseDTO;

import java.util.List;
import java.util.Optional;

public interface WorkHistoryRepository extends JpaRepository<WorkHistory, Long>, JpaSpecificationExecutor<WorkHistory> {
    Optional<WorkHistory> findByEmpIdAndYearAndMonth(@Param("empId") Long empId, @Param("year") int year, @Param("month") int month);

    Page<WorkResponseDTO> findAllByEmpId(@Param("empId") Long empId, Pageable pageable);

    boolean existsByEmpIdAndYearAndMonth(@Param("empId") Long empId, @Param("year") int year, @Param("month") int month);

    List<WorkHistory> findByEmpIdAndYear(@Param("empId") Long empId, @Param("year") int year);
}
