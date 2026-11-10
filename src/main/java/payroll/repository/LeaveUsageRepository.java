package payroll.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import payroll.entity.LeaveUsage;

import java.time.LocalDate;
import java.util.Optional;

public interface LeaveUsageRepository extends JpaRepository<LeaveUsage, Long>, JpaSpecificationExecutor<LeaveUsage> {
    @Query(value = """
    SELECT EXISTS(
        SELECT 1 FROM leave_usage l
        WHERE l.id != :id AND
            l.emp_id = :empId AND
            l.delete_yn = 'N' AND
            l.start_date <= :endDate AND
            l.end_date >= :startDate)
    """, nativeQuery = true)
    boolean existsLeaveUsage(@Param("id") Long id, @Param("empId") Long empId,
                                  @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    Optional<LeaveUsage> findByIdAndDeleteYn(@Param("id") Long id, @Param("deleteYn") String deleteYn);

    Long findEmpIdById(@Param("id") Long id);
}