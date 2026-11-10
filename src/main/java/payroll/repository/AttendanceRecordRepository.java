package payroll.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import payroll.entity.AttendanceRecord;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;


public interface AttendanceRecordRepository extends JpaRepository<AttendanceRecord, Long>, JpaSpecificationExecutor<AttendanceRecord> {
    boolean existsByEmpIdAndWorkDate(@Param("empId") Long empId, @Param("workDate") LocalDate workDate);

    Optional<AttendanceRecord> findByEmpIdAndWorkDate(
            @Param("empId") Long empId, @Param("workDate") LocalDate workDate);

    @Query("""
    SELECT a FROM AttendanceRecord a
    WHERE a.empId = :empId
      AND a.workDate >= :start
      AND a.workDate < :end
    """)
    List<AttendanceRecord> findByEmpIdPeriod(@Param("empId") Long empId, @Param("start") LocalDate start, @Param("end") LocalDate end);

    @Query(value = """
    SELECT EXISTS(
        SELECT 1 FROM attendance_record a
        WHERE a.empId = :empId AND
            a.workDate >= :startDate AND
            a.workDate <= :endDate AND
            a.status != '휴가')
    """, nativeQuery = true)
    boolean existsWorkedAttendance(@Param("empId") Long empId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    boolean existsByEmpIdAndWorkDate(@Param("empId") Set<Long> empId, Set<LocalDate> workDate);

    boolean findByCodeIn(@Param("empCode") Set<String> empCode);

    List<AttendanceRecord> findByEmpIdInAndWorkDateIn(Set<Long> empIds, Set<LocalDate> workDates);



}