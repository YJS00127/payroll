package payroll.payroll;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import payroll.common.dto.ResultDTO;
import payroll.payroll.dto.PayrollRequestDTO;
import payroll.payroll.dto.PayrollResponseDTO;
import payroll.payroll.dto.PayrollSearchDTO;
import payroll.payroll.dto.PayslipResponseDTO;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PayrollController {
    private final PayrollService payrollService;

    @GetMapping("/payroll")
    public ResponseEntity<Page<PayrollResponseDTO>> findAllPayrollHistory(@ModelAttribute PayrollSearchDTO searchDTO,
                                                                          @PageableDefault(size=10)Pageable pageable){
        return ResponseEntity.ok(payrollService.findPayroll(searchDTO, pageable));
    }

    @GetMapping("/payroll/{id}")
    public ResponseEntity<PayrollResponseDTO> findPayrollHistoryById(@PathVariable Long id){
        return ResponseEntity.ok(payrollService.findPayrollById(id));
    }

    @GetMapping("/payroll/excel")
    public ResponseEntity<byte[]> downloadPayrollExcel() throws IOException{
        byte[] excelData = payrollService.downloadPayrollExcel();

        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=급여 엑셀 등록 양식.xlsx")
                .contentType(MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(excelData);
    }

    @GetMapping("/payslip/{id}")
    public ResponseEntity<PayslipResponseDTO> findPayslipDataById(@PathVariable Long id){
        return ResponseEntity.ok(payrollService.findPayslipDataById(id));
    }

    @PostMapping("/payroll")
    public ResponseEntity<ResultDTO> createPayrollHistory(@RequestBody @Valid PayrollRequestDTO requestDTO){
        return ResponseEntity.ok(payrollService.savePayroll(requestDTO));
    }

    @PutMapping("/payroll/{id}")
    public ResponseEntity<ResultDTO> updatePayrollHistory(@RequestBody @Valid PayrollRequestDTO requestDTO,
                                                          @PathVariable Long id){
        return ResponseEntity.ok(payrollService.updatePayroll(requestDTO, id));
    }

    @DeleteMapping("/payroll/{id}")
    public ResponseEntity<ResultDTO> deletePayrollHistory(@PathVariable Long id){
        return ResponseEntity.ok(payrollService.deletePayroll(id));
    }

    @DeleteMapping("/payroll")
    public ResponseEntity<ResultDTO> deleteSelectedPayroll(@RequestBody List<Long> ids){
        return ResponseEntity.ok(payrollService.deleteSelectedPayroll(ids));
    }
}
