package payroll.department;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import payroll.department.dto.DeptCreateRequestDTO;
import payroll.entity.Department;
import payroll.repository.DepartmentRepository;

@Service
@RequiredArgsConstructor
public class DepartmentServiceImpl implements DepartmentService{

    private final DepartmentRepository departmentRepository;

    @Override
    public void insertDept(DeptCreateRequestDTO requestDTO){
        
        Department department = Department.builder()
                .name(requestDTO.getName())
                .code(requestDTO.getCode())
                .createdDeptDate(requestDTO.getCreatedDeptDate())
                .build();

        departmentRepository.save(department);
    }
}
