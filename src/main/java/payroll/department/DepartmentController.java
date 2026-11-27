package payroll.department;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import payroll.common.dto.ResultDTO;
import payroll.department.dto.DeptRequestDTO;
import payroll.department.dto.DeptResponseDTO;
import payroll.department.dto.DeptSearchDTO;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/dept")
public class DepartmentController {

    private final DepartmentService deptService;

    @GetMapping
    public ResponseEntity<List<DeptResponseDTO>> getAllDept(@ModelAttribute DeptSearchDTO searchDTO){
        return ResponseEntity.ok(deptService.getAllDept(searchDTO));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DeptResponseDTO> getDeptById(@PathVariable Long id){
        return ResponseEntity.ok(deptService.getDeptById(id));
    }

    @PostMapping
    public ResponseEntity<ResultDTO> createDept(@RequestBody @Valid DeptRequestDTO request){
        return ResponseEntity.ok(deptService.saveDept(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResultDTO> updateDept(@RequestBody @Valid DeptRequestDTO request,
                                                @PathVariable Long id){
        return ResponseEntity.ok(deptService.updateDept(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResultDTO> deleteDept(@PathVariable Long id){
        return ResponseEntity.ok(deptService.deleteDept(id));
    }

    @DeleteMapping
    public ResponseEntity<ResultDTO> deleteDeptAll(@RequestBody List<Long> ids){
        return ResponseEntity.ok(deptService.deleteDeptAll(ids));
    }

}
