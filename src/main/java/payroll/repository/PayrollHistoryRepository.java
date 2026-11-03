package payroll.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.query.Param;
import payroll.entity.PayrollHistory;

public interface PayrollHistoryRepository extends JpaRepository<PayrollHistory, Long>, JpaSpecificationExecutor<PayrollHistory> {
    boolean existsByEmpIdAndYearAndMonth(@Param("empId") Long empId, @Param("year") int year, @Param("month") int month);
}
