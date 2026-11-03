package payroll.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import payroll.entity.SalaryHistory;

public interface SalaryHistoryRepository extends JpaRepository<SalaryHistory, Long>, JpaSpecificationExecutor<SalaryHistory> {
}
