package payroll.department;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import payroll.common.dto.ResultDTO;
import payroll.department.dto.DeptRequestDTO;
import payroll.department.dto.DeptResponseDTO;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class DepartmentController {

    private final DepartmentService deptService;

    @GetMapping("/dept")
    public ResponseEntity<List<DeptResponseDTO>> getAllDept(){
        return ResponseEntity.ok(deptService.getAllDept());
    }

    @GetMapping("/dept/{id}")
    public ResponseEntity<DeptResponseDTO> getDeptById(@PathVariable Long id){
        return ResponseEntity.ok(deptService.getDeptById(id));
    }

    @PostMapping("/dept")
    public ResponseEntity<ResultDTO> createDept(@RequestBody @Valid DeptRequestDTO request){
        return ResponseEntity.ok(deptService.saveDept(request));
    }

    @PutMapping("dept/{id}")
    public ResponseEntity<ResultDTO> updateDept(@RequestBody @Valid DeptRequestDTO request,
                                                @PathVariable Long id){
        return ResponseEntity.ok(deptService.updateDept(id, request));
    }

    @DeleteMapping("/dept/{id}")
    public ResponseEntity<ResultDTO> deleteDept(@PathVariable Long id){
        return ResponseEntity.ok(deptService.deleteDept(id));
    }

    @DeleteMapping("/depts")
    public ResponseEntity<ResultDTO> deleteDeptAll(@RequestBody List<Long> ids){
        return ResponseEntity.ok(deptService.deleteDeptAll(ids));
    }

}
