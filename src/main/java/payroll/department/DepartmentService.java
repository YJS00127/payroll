package payroll.department;

import payroll.common.dto.ResultDTO;
import payroll.department.dto.DeptRequestDTO;
import payroll.department.dto.DeptResponseDTO;
import payroll.department.dto.DeptSearchDTO;

import java.util.List;

public interface DepartmentService {

    List<DeptResponseDTO> getAllDept(DeptSearchDTO searchDTO);

    DeptResponseDTO getDeptById(Long id);

    ResultDTO saveDept(DeptRequestDTO deptRequestDTO);

    ResultDTO updateDept(Long id, DeptRequestDTO deptRequestDTO);

    ResultDTO deleteDept(Long id);

    ResultDTO deleteDeptAll(List<Long> ids);
}
