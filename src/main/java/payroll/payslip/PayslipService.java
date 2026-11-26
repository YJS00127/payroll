package payroll.payslip;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import payroll.common.dto.ResultDTO;
import payroll.payslip.dto.PayslipDateRequestDTO;
import payroll.payslip.dto.PayslipResponseDTO;
import payroll.payslip.dto.PayslipSearchDTO;

import java.util.List;

public interface PayslipService {
    Page<PayslipResponseDTO> findPayslip(PayslipSearchDTO searchDTO, Pageable pageable);

    ResultDTO saveConfirmedAt(PayslipDateRequestDTO requestDTO);

    ResultDTO savePaymentDate(PayslipDateRequestDTO requestDTO);

    ResultDTO deleteConfirmedAt(List<Long> ids);
}
