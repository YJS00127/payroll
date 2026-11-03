package payroll.employee;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import payroll.employee.dto.*;

public interface EmployeeService {
    Page<EmpResponseDTO> findAllEmp(EmpSearchDTO searchDTO, Pageable pageable);

    EmpResultDTO createEmp(EmpRequestDTO requestDTO);

    EmpResultDTO updateEmp(EmpUpdateRequestDTO requestDTO);

    EmpResultDTO deleteEmp(Long id);
}
