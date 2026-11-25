package payroll.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import payroll.entity.LeaveRecord;

import java.util.Optional;

public interface LeaveRecordRepository extends JpaRepository<LeaveRecord, Long>, JpaSpecificationExecutor<LeaveRecord> {
    Optional<LeaveRecord> findByEmpIdAndYearAndMonth(Long empId, int year, int month);

    int findRemainingLeaveByEmpIdAndYear(@Param("empId")Long empId, @Param("year")int year);

    @Query(value = """
    SELECT SUM(remaining_leave)
    FROM leave_record
    WHERE emp_id = :empId
      AND year BETWEEN :startYear AND :endYear
    """, nativeQuery = true)
    int sumRemainingLeave(@Param("empId") Long empId, @Param("startYear") int startYear, @Param("endYear") int endYear);

    @Query(value = """
        SELECT total_leave FROM leave_record
        WHERE emp_id = :empId AND
            (year < :year OR (year = :year AND month < :month))
        ORDER BY year DESC, month DESC
        LIMIT 1
    """, nativeQuery = true)
    Optional<Double> findPreviousTotalLeave(@Param("empId") Long empId, @Param("year")int year, @Param("month")int month);


}
