package payroll.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import payroll.entity.InsuranceRateHistory;

import java.time.LocalDate;
import java.util.Optional;

public interface InsuranceRateHistoryRepository extends JpaRepository<InsuranceRateHistory, Long>, JpaSpecificationExecutor<InsuranceRateHistory> {

    @Query(value = """
    SELECT EXISTS (
        SELECT 1 FROM insurance_rate_history i
            i.effectiveDate = :effectiveDate)
    """, nativeQuery = true)
    boolean existsInsuranceRateHistory(@Param("effectiveDate")LocalDate effectiveDate);

    @Query("SELECT i FROM InsuranceRateHistory i ORDER BY effectiveDate DESC LIMIT 1")
    Optional<InsuranceRateHistory> findRecentInsuranceRate();

    @Query("SELECT i FROM InsuranceRateHistory i WHERE i.effectiveDate<= :date ORDER BY effectiveDate DESC LIMIT 1")
    Optional<InsuranceRateHistory> findInsuranceRate(@Param("date") LocalDate date);
}