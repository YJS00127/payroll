package payroll.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import payroll.entity.LeaveRecord;

import java.util.Optional;

public interface LeaveHistoryRepository extends JpaRepository<LeaveRecord, Long>, JpaSpecificationExecutor<LeaveRecord> {
    boolean existsByEmpIdAndYearAndMonth(@Param("empId") Long empId, @Param("year") int year, @Param("month") int month);

    Optional<LeaveRecord> findByEmpIdAndYearAndMonth(Long empId, int year, int month);

    @Modifying
    @Query("UPDATE LeaveRecord l SET l.deleteYn = 'Y' WHERE l.empId = :id")
    void delete(@Param("id") Long id);


}
