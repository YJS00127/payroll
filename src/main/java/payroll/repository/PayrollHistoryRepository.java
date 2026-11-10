package payroll.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import payroll.entity.PayrollHistory;

import java.util.List;
import java.util.Optional;

public interface PayrollHistoryRepository extends JpaRepository<PayrollHistory, Long>, JpaSpecificationExecutor<PayrollHistory> {
    boolean existsByEmpIdAndYearAndMonth(@Param("empId") Long empId, @Param("year") int year, @Param("month") int month);

    Optional<PayrollHistory> findByEmpIdAndYearAndMonth(@Param("empId") Long empId, @Param("year") int year, @Param("month")int month);

    @Query(value = "SELECT SUM(gross_salary) FROM payroll_history WHERE emp_id = :empId AND year = :year", nativeQuery = true)
    Optional<Integer> sumYearGrossSalary(@Param("empId") Long empId, @Param("year") int year);

    @Query(value = "SELECT SUM(health_insurance) FROM payroll_history WHERE emp_id = :empId AND year = :year", nativeQuery = true)
    Optional<Integer> sumYearHealthInsurance(@Param("empId")Long empId, @Param("year")int year);
}
