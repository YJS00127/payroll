package payroll.salaryHistory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import payroll.common.dto.ResultDTO;
import payroll.salaryHistory.dto.SalaryHistoryRequestDTO;
import payroll.salaryHistory.dto.SalaryHistoryResponseDTO;
import payroll.salaryHistory.dto.SalaryHistorySearchDTO;

import java.time.LocalDate;

public interface SalaryHistoryService {
    Page<SalaryHistoryResponseDTO> findSalaryHistory(SalaryHistorySearchDTO searchDTO, Pageable pageable);

    void saveSalaryHistory(Long empId, int previousHourlyWage, int newHourlyWage,
                                LocalDate effectiveDate, String reason);

    ResultDTO updateSalaryHistory(Long id, SalaryHistoryRequestDTO requestDTO);

//    ResultDTO deleteSalaryHistory(Long id);
}
