package payroll.employee;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import payroll.employee.dto.*;


@RestController
@RequestMapping("/api/emp")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    @GetMapping
    public ResponseEntity<Page<EmpResponseDTO>> getAllEmp(@RequestBody EmpSearchDTO searchDTO,
                                                          @PageableDefault(size=10) Pageable pageable){
        return ResponseEntity.ok(employeeService.findAllEmp(searchDTO, pageable));
    }

    @PostMapping
    public ResponseEntity<EmpResultDTO> createEmp(@RequestBody @Valid EmpRequestDTO requestDTO){
        return ResponseEntity.ok(employeeService.createEmp(requestDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmpResultDTO> updateEmp(@RequestBody @Valid EmpUpdateRequestDTO requestDTO){
        return ResponseEntity.ok(employeeService.updateEmp(requestDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<EmpResultDTO> deleteEmp(Long id){
        return ResponseEntity.ok(employeeService.deleteEmp(id));
    }
}
