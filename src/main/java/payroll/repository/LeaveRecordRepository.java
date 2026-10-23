package payroll.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import payroll.entity.LeaveRecord;

public interface LeaveRecordRepository extends JpaRepository<LeaveRecord, Long> {
}
