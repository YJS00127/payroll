package payroll.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import payroll.entity.PayrollRecord;

public interface PayrollRecordRepository extends JpaRepository<PayrollRecord, Long> {
}
