package payroll.department;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import payroll.common.dto.ResultDTO;
import payroll.department.dto.DeptRequestDTO;
import payroll.department.dto.DeptResponseDTO;
import payroll.department.dto.DeptSearchDTO;

import java.util.List;

public interface DepartmentService {

    Page<DeptResponseDTO> findAllDept(DeptSearchDTO searchDTO, Pageable pageable);

    ResultDTO saveDept(DeptRequestDTO deptRequestDTO);

    ResultDTO updateDept(Long id, DeptRequestDTO deptRequestDTO);

    ResultDTO deleteDept(Long id);

    ResultDTO deleteDeptAll(List<Long> id);
}
