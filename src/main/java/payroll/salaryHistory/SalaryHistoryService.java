package payroll.salaryHistory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import payroll.salaryHistory.dto.SalaryHistoryResponseDTO;
import payroll.salaryHistory.dto.SalaryHistoryResultDTO;
import payroll.salaryHistory.dto.SalaryHistorySearchDTO;

import java.time.LocalDate;

public interface SalaryHistoryService {
    Page<SalaryHistoryResponseDTO> findSalaryHistory(SalaryHistorySearchDTO searchDTO, Pageable pageable);

    SalaryHistoryResultDTO saveSalaryHistory(Long empId, int previousHourlyWage, int newHourlyWage,
                                             LocalDate effectiveDate, String reason);

    SalaryHistoryResultDTO deleteSalaryHistory(Long id);
}
