package payroll.department;

import payroll.common.dto.ResultDTO;
import payroll.department.dto.DeptRequestDTO;
import payroll.department.dto.DeptResponseDTO;

import java.util.List;

public interface DepartmentService {

    List<DeptResponseDTO> getAllDept();

    DeptResponseDTO getDeptById(Long id);

    ResultDTO saveDept(DeptRequestDTO deptRequestDTO);

    ResultDTO updateDept(Long id, DeptRequestDTO deptRequestDTO);

    ResultDTO deleteDept(Long id);

    ResultDTO deleteDeptAll(List<Long> ids);
}
