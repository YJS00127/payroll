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

    SalaryHistoryResponseDTO findSalaryHistoryById(Long id);

    ResultDTO saveSalaryHistory(SalaryHistoryRequestDTO requestDTO);

    ResultDTO updateSalaryHistory(Long id, SalaryHistoryRequestDTO requestDTO);

}
