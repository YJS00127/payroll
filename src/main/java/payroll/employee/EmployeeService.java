package payroll.employee;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import payroll.common.dto.ResultDTO;
import payroll.employee.dto.*;

import java.util.List;

public interface EmployeeService {
    Page<EmpResponseDTO> findAllEmp(EmpSearchDTO searchDTO, Pageable pageable);

    ResultDTO createEmp(EmpRequestDTO requestDTO);

    ResultDTO createEmpAll(List<EmpRequestDTO> requestDTOs);

    ResultDTO updateEmp(EmpUpdateRequestDTO requestDTO);

    ResultDTO deleteEmp(Long id);

    ResultDTO deleteEmpAll(List<Long> id);
}
