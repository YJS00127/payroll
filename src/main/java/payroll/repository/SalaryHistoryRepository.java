package payroll.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import payroll.entity.SalaryHistory;

public interface SalaryHistoryRepository extends JpaRepository<SalaryHistory, Long> {
}
