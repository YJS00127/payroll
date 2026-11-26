package payroll.payslip;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import payroll.common.dto.ResultDTO;
import payroll.payroll.PayrollService;
import payroll.payroll.dto.PayslipPDFResponseDTO;
import payroll.payslip.dto.PayslipDateRequestDTO;
import payroll.payslip.dto.PayslipResponseDTO;
import payroll.payslip.dto.PayslipSearchDTO;

import java.util.List;

@RequestMapping("/api/payslip")
@AllArgsConstructor
@RestController
public class PayslipController {
    private final PayrollService payrollService;
    private final PayslipService payslipService;

    @GetMapping
    public ResponseEntity<Page<PayslipResponseDTO>> getPayslip(@ModelAttribute PayslipSearchDTO searchDTO,
                                                               @PageableDefault(size=10)Pageable pageable){
        return ResponseEntity.ok(payslipService.findPayslip(searchDTO, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PayslipPDFResponseDTO> findPayslipDataById(@PathVariable Long id){
        return ResponseEntity.ok(payrollService.findPayslipDataById(id));
    }

    @PutMapping("/confirm")
    public ResponseEntity<ResultDTO> updateConfirmedAt(@RequestBody PayslipDateRequestDTO requestDTO){
        return ResponseEntity.ok(payslipService.saveConfirmedAt(requestDTO));
    }

    @PutMapping("/payment")
    public ResponseEntity<ResultDTO> updatePaymentDate(@RequestBody PayslipDateRequestDTO requestDTO){
        return ResponseEntity.ok(payslipService.savePaymentDate(requestDTO));
    }

    @PutMapping("/cancel")
    public ResponseEntity<ResultDTO> cancelConfirmedAt(@RequestBody List<Long> ids){
        return ResponseEntity.ok(payslipService.deleteConfirmedAt(ids));
    }
}
