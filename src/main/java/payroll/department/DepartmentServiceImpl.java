package payroll.department;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import payroll.common.dto.ResultDTO;
import payroll.common.exception.EmployeeInDepartmentException;
import payroll.department.dto.DeptRequestDTO;
import payroll.department.dto.DeptResponseDTO;
import payroll.entity.Department;
import payroll.repository.DepartmentRepository;
import payroll.repository.EmployeeRepository;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class DepartmentServiceImpl implements DepartmentService{

    private final DepartmentRepository departmentRepository;
    private final EmployeeRepository employeeRepository;

    @Override
    public List<DeptResponseDTO> getAllDept(){
        return departmentRepository.findAllByDeleteYn("N").stream()
                .map(DeptResponseDTO::new)
                .toList();
    }

    @Override
    public DeptResponseDTO getDeptById(Long id){
        Department dept = departmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 부서입니다"));

        return new DeptResponseDTO(dept);
    }

    @Override
    @Transactional
    public ResultDTO saveDept(DeptRequestDTO requestDTO){
        Department department = requestDTO.toEntity();

        if(departmentRepository.existsByCode(department.getCode())){
            throw new EntityExistsException("해당 코드의 부서가 이미 존재합니다");
        }

        departmentRepository.save(department);

        return new ResultDTO("success", "부서 등록 성공");
    }

    @Override
    @Transactional
    public ResultDTO updateDept(Long id, DeptRequestDTO requestDTO){
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("부서가 존재하지 않습니다"));
        department.update(requestDTO);

        return new ResultDTO("success", "부서 수정 성공");
    }

    @Override
    @Transactional
    public ResultDTO deleteDept(Long id){
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("부서가 존재하지 않습니다"));

        if(employeeRepository.existsByDeptIdAndDeleteYn(id, "N")){
            throw new EmployeeInDepartmentException("해당 부서에 재직 중인 사원이 소속돼있어 삭제할 수 없습니다");
        }

        department.delete();

        return new ResultDTO("success", "부서 삭제 성공");
    }

    @Override
    @Transactional
    public ResultDTO deleteDeptAll(List<Long> ids){
        List<Department> departments = departmentRepository.findAllById(ids);

        if(departments.size() != Set.copyOf(ids).size()
        || departments.stream().anyMatch(dept -> dept.getDeleteYn().equals("Y"))){
            throw new EntityNotFoundException("존재하지 않는 부서가 포함되어있습니다");
        }

        if(employeeRepository.existsByDeptIdInAndDeleteYn(ids, "N")){
            throw new EmployeeInDepartmentException("선택된 부서 중 재직 중인 사원이 소속돼있어 삭제할 수 없습니다");
        }

        departments.forEach(Department::delete);

        return new ResultDTO("success", "부서 삭제 성공");
    }
}
