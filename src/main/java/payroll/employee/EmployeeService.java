package payroll.employee;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import payroll.common.dto.ResultDTO;
import payroll.employee.dto.*;

import java.io.IOException;
import java.util.List;

public interface EmployeeService {
    Page<EmpResponseDTO> findAllEmp(EmpSearchDTO searchDTO, Pageable pageable);

    EmpResponseDTO findEmpById(Long id);

    byte[] downloadEmpExcel() throws IOException;

    ResultDTO createEmp(EmpRequestDTO requestDTO);

    ResultDTO createEmpAll(List<EmpRequestDTO> requestDTOs);

    ResultDTO updateEmp(Long id, EmpUpdateRequestDTO requestDTO);

    void updateHourlyWage(Long id, int hourlyWage);

    ResultDTO deleteEmp(Long id);

    ResultDTO deleteEmps(List<Long> id);
}
