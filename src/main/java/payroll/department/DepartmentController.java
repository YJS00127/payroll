package payroll.department;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import payroll.department.DepartmentService;

import org.springframework.web.bind.annotation.*;
import payroll.department.dto.DeptCreateRequestDTO;
import payroll.department.dto.DeptCreateResponseDTO;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/department")
public class DepartmentController {

    private final DepartmentService deptService;

    @PostMapping
    public DeptCreateResponseDTO createDept(@Valid @RequestBody DeptCreateRequestDTO request){


    }
}
