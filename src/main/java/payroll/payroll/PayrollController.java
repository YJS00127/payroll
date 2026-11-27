package payroll.payroll;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import payroll.common.dto.ResultDTO;
import payroll.payroll.dto.PayrollRequestDTO;
import payroll.payroll.dto.PayrollResponseDTO;
import payroll.payroll.dto.PayrollSearchDTO;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/api/payroll")
@RequiredArgsConstructor
public class PayrollController {
    private final PayrollService payrollService;

    @GetMapping
    public ResponseEntity<Page<PayrollResponseDTO>> findAllPayrollHistory(@ModelAttribute PayrollSearchDTO searchDTO,
                                                                          @PageableDefault(size=10)Pageable pageable){
        return ResponseEntity.ok(payrollService.findPayroll(searchDTO, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PayrollResponseDTO> findPayrollHistoryById(@PathVariable Long id){
        return ResponseEntity.ok(payrollService.findPayrollById(id));
    }

    @GetMapping("/excel")
    public void downloadPayrollExcel(HttpServletResponse response) throws IOException{

        response.setHeader(
                HttpHeaders.CONTENT_DISPOSITION,
                ContentDisposition.attachment()
                        .filename("급여 엑셀 등록 양식.xlsx", StandardCharsets.UTF_8)
                        .build().toString()
        );

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

        payrollService.downloadPayrollExcel(response);
    }

    @PostMapping
    public ResponseEntity<ResultDTO> createPayrollHistory(@RequestBody @Valid PayrollRequestDTO requestDTO){
        return ResponseEntity.ok(payrollService.savePayroll(requestDTO));
    }

    @PostMapping("/excel")
    public ResponseEntity<ResultDTO> uploadPayrollExcel(MultipartFile file) throws IOException {
        return ResponseEntity.ok(payrollService.uploadPayrollExcel(file));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResultDTO> updatePayrollHistory(@RequestBody @Valid PayrollRequestDTO requestDTO,
                                                          @PathVariable Long id){
        return ResponseEntity.ok(payrollService.updatePayroll(requestDTO, id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResultDTO> deletePayrollHistory(@PathVariable Long id){
        return ResponseEntity.ok(payrollService.deletePayroll(id));
    }

    @DeleteMapping
    public ResponseEntity<ResultDTO> deleteSelectedPayroll(@RequestBody List<Long> ids){
        return ResponseEntity.ok(payrollService.deleteSelectedPayroll(ids));
    }
}
