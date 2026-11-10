package payroll.employee;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import payroll.common.dto.ResultDTO;
import payroll.employee.dto.*;

import java.util.List;


@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    @GetMapping("/emp")
    public ResponseEntity<Page<EmpResponseDTO>> getAllEmp(@ModelAttribute EmpSearchDTO searchDTO,
                                                          @PageableDefault(size=10) Pageable pageable){
        return ResponseEntity.ok(employeeService.findAllEmp(searchDTO, pageable));
    }

    @PostMapping("/emp")
    public ResponseEntity<ResultDTO> createEmp(@RequestBody @Valid EmpRequestDTO requestDTO){
        return ResponseEntity.ok(employeeService.createEmp(requestDTO));
    }

    @PostMapping("/emps")
    public ResponseEntity<ResultDTO> createEmpAll(@RequestBody @Valid List<EmpRequestDTO> requestDTOs){
        return ResponseEntity.ok(employeeService.createEmpAll(requestDTOs));
    }

    @PutMapping("/emp/{id}")
    public ResponseEntity<ResultDTO> updateEmp(@RequestBody @Valid EmpUpdateRequestDTO requestDTO){
        return ResponseEntity.ok(employeeService.updateEmp(requestDTO));
    }

    @DeleteMapping("/emp/{id}")
    public ResponseEntity<ResultDTO> deleteEmp(Long id){
        return ResponseEntity.ok(employeeService.deleteEmp(id));
    }

    @DeleteMapping("/emps")
    public ResponseEntity<ResultDTO> deleteEmpAll(@RequestBody List<Long> id){
        return ResponseEntity.ok(employeeService.deleteEmpAll(id));
    }
}
