package payroll.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import payroll.entity.LeaveUsage;

import java.time.LocalDate;
import java.util.List;
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
    Integer existsLeaveUsage(@Param("id") Long id, @Param("empId") Long empId,
                                  @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    Optional<LeaveUsage> findByIdAndDeleteYn(@Param("id") Long id, @Param("deleteYn") String deleteYn);

    @Query(value = """
    SELECT * FROM leave_usage l
    WHERE l.emp_id = :empId AND
        l.start_date <= :end AND
        l.end_date >= :start AND
        l.delete_yn = 'N'
    """, nativeQuery = true)
    List<LeaveUsage> findLeave(@Param("empId")Long empId, @Param("start") LocalDate start,
                                          @Param("end") LocalDate end);
}