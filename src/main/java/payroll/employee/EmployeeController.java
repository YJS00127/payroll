package payroll.employee;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import payroll.common.dto.ResultDTO;
import payroll.employee.dto.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;


@RestController
@RequestMapping("/api/emp")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    @GetMapping
    public ResponseEntity<Page<EmpResponseDTO>> getAllEmp(@ModelAttribute EmpSearchDTO searchDTO,
                                                          @PageableDefault(size=10) Pageable pageable){
        return ResponseEntity.ok(employeeService.findAllEmp(searchDTO, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmpResponseDTO> getEmpById(@PathVariable Long id){
        return ResponseEntity.ok(employeeService.findEmpById(id));
    }

    @GetMapping("/excel")
    public void downloadEmpExcel(HttpServletResponse response) throws IOException {
        response.setHeader(
                HttpHeaders.CONTENT_DISPOSITION,
                ContentDisposition.attachment()
                        .filename("사원 엑셀 등록 양식.xlsx", StandardCharsets.UTF_8)
                        .build().toString()
                );
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

        employeeService.downloadEmpExcel(response);
    }

    @PostMapping
    public ResponseEntity<ResultDTO> createEmp(@RequestBody @Valid EmpRequestDTO requestDTO){
        return ResponseEntity.ok(employeeService.saveEmp(requestDTO));
    }

    @PostMapping("/excel")
    public ResponseEntity<ResultDTO> uploadEmpExcel(@RequestParam("file") MultipartFile file) throws IOException{
        return ResponseEntity.ok(employeeService.uploadEmpExcel(file));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResultDTO> updateEmp(@PathVariable Long id,
                                               @RequestBody @Valid EmpUpdateRequestDTO requestDTO){
        return ResponseEntity.ok(employeeService.updateEmp(id, requestDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResultDTO> deleteEmp(@PathVariable Long id){
        return ResponseEntity.ok(employeeService.deleteEmp(id));
    }

    @DeleteMapping
    public ResponseEntity<ResultDTO> deleteEmpAll(@RequestBody List<Long> id){
        return ResponseEntity.ok(employeeService.delSelectedEmp(id));
    }
}
