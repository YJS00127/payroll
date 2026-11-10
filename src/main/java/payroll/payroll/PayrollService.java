package payroll.payroll;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import payroll.common.dto.ResultDTO;
import payroll.payroll.dto.PayrollRequestDTO;
import payroll.payroll.dto.PayrollResponseDTO;
import payroll.payroll.dto.PayrollSearchDTO;

public interface PayrollService {
    Page<PayrollResponseDTO> findPayroll(PayrollSearchDTO searchDTO, Pageable pageable);

    ResultDTO savePayroll(PayrollRequestDTO requestDTO);

    ResultDTO updatePayroll(PayrollRequestDTO requestDTO);
}
