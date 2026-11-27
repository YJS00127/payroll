package payroll.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import payroll.entity.LeaveRecord;

import java.util.Optional;

public interface LeaveRecordRepository extends JpaRepository<LeaveRecord, Long>, JpaSpecificationExecutor<LeaveRecord> {
    Optional<LeaveRecord> findByEmpIdAndYearAndMonth(@Param("empId") Long empId, @Param("year") int year, @Param("month") int month);

    Optional<Double> findRemainingLeaveByEmpIdAndYearAndMonth(@Param("empId")Long empId, @Param("year")int year, @Param("month")int month);

    @Query(value = """
        SELECT total_leave FROM leave_record
        WHERE emp_id = :empId AND
            (year < :year OR (year = :year AND month < :month))
        ORDER BY year DESC, month DESC
        LIMIT 1
    """, nativeQuery = true)
    Optional<Double> findPreviousTotalLeave(@Param("empId") Long empId, @Param("year")int year, @Param("month")int month);


}
