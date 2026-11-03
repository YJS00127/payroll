package payroll.department;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import payroll.department.dto.DeptRequestDTO;
import payroll.department.dto.DeptResponseDTO;
import payroll.department.dto.DeptResultDTO;
import payroll.department.dto.DeptSearchDTO;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/dept")
public class DepartmentController {

    private final DepartmentService deptService;

    @GetMapping
    public ResponseEntity<Page<DeptResponseDTO>> getAllDepts(@PageableDefault(size=10) Pageable pageable,
                                                             @RequestBody DeptSearchDTO searchDTO){

        return ResponseEntity.ok(deptService.findAllDepts(searchDTO, pageable));
    }

    @PostMapping
    public ResponseEntity<DeptResultDTO> createDept(@RequestBody @Valid DeptRequestDTO request){
        return ResponseEntity.ok(deptService.saveDept(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DeptResultDTO> updateDept(@RequestBody @Valid DeptRequestDTO request,
                                                      Long id){
        return ResponseEntity.ok(deptService.updateDept(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<DeptResultDTO> deleteDept(Long id){
        return ResponseEntity.ok(deptService.deleteDept(id));
    }

}
