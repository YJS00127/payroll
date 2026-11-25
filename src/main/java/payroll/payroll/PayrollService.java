package payroll.payroll;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import payroll.common.dto.ResultDTO;
import payroll.payroll.dto.PayrollRequestDTO;
import payroll.payroll.dto.PayrollResponseDTO;
import payroll.payroll.dto.PayrollSearchDTO;
import payroll.payroll.dto.PayslipResponseDTO;

import java.io.IOException;
import java.util.List;

public interface PayrollService {
    Page<PayrollResponseDTO> findPayroll(PayrollSearchDTO searchDTO, Pageable pageable);

    PayrollResponseDTO findPayrollById(Long id);

    PayslipResponseDTO findPayslipDataById(Long payrollId);

    ResultDTO savePayroll(PayrollRequestDTO requestDTO);

    byte[] downloadPayrollExcel() throws IOException;

    ResultDTO updatePayroll(PayrollRequestDTO requestDTO, Long id);

    ResultDTO deletePayroll(Long id);

    ResultDTO deleteSelectedPayroll(List<Long> ids);
}
