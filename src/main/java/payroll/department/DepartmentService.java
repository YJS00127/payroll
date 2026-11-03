package payroll.department;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import payroll.department.dto.DeptRequestDTO;
import payroll.department.dto.DeptResponseDTO;
import payroll.department.dto.DeptResultDTO;
import payroll.department.dto.DeptSearchDTO;

public interface DepartmentService {

    Page<DeptResponseDTO> findAllDepts(DeptSearchDTO searchDTO, Pageable pageable);

    DeptResultDTO saveDept(DeptRequestDTO deptRequestDTO);

    DeptResultDTO updateDept(Long id, DeptRequestDTO deptRequestDTO);

    DeptResultDTO deleteDept(Long id);
}
