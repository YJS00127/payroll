package payroll.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import payroll.entity.WorkRecord;

public interface WorkRecordRepository extends JpaRepository<WorkRecord, Long> {
}
