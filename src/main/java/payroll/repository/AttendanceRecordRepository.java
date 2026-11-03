package payroll.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import payroll.entity.AttendanceRecord;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


public interface AttendanceRecordRepository extends JpaRepository<AttendanceRecord, Long>, JpaSpecificationExecutor<AttendanceRecord> {
    List<AttendanceRecord> findAllByEmpIdAndWorkDate(@Param("empId") Long empId, @Param("workDate") LocalDate workDate);

    boolean existsByEmpIdAndWorkDate(@Param("empId") Long empId, @Param("workDate") LocalDate workDate);

    Optional<AttendanceRecord> findByEmpIdAndWorkDateAndDeleteYn(
            @Param("empId") Long empId, @Param("workDate") LocalDate workDate,
            @Param("deleteYn") String deleteYn
    );
    @Query("""
    SELECT a FROM AttendanceRecord a
    WHERE a.empId = :empId
      AND a.workDate >= :start
      AND a.workDate < :end
      AND a.deleteYn = 'N'
    """)
    List<AttendanceRecord> findByEmpIdPeriod(@Param("empId") Long empId, @Param("start") LocalDate start, @Param("end") LocalDate end);
}