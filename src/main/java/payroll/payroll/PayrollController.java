package payroll.payroll;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import payroll.payroll.dto.PayrollRequestDTO;
import payroll.payroll.dto.PayrollResponseDTO;
import payroll.payroll.dto.PayrollResultDTO;
import payroll.payroll.dto.PayrollSearchDTO;

@RestController
@RequestMapping("/api/payroll")
@RequiredArgsConstructor
public class PayrollController {
    private final PayrollService payrollService;

    @GetMapping
    public ResponseEntity<Page<PayrollResponseDTO>> findAllPayrollHistory(@RequestBody PayrollSearchDTO searchDTO,
                                                                          @PageableDefault(size=10)Pageable pageable){
        return ResponseEntity.ok(payrollService.findPayroll(searchDTO, pageable));
    }

    @PostMapping
    public ResponseEntity<PayrollResultDTO> createPayrollHistory(@RequestBody @Valid PayrollRequestDTO requestDTO){
        return ResponseEntity.ok(payrollService.savePayroll(requestDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PayrollResultDTO> updatePayrollHistory(@RequestBody @Valid PayrollRequestDTO requestDTO){
        return ResponseEntity.ok(payrollService.updatePayroll(requestDTO));
    }
}
