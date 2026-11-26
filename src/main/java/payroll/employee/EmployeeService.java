package payroll.employee;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;
import payroll.common.dto.ResultDTO;
import payroll.employee.dto.*;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public interface EmployeeService {
    Page<EmpResponseDTO> findAllEmp(EmpSearchDTO searchDTO, Pageable pageable);

    EmpResponseDTO findEmpById(Long id);

    void downloadEmpExcel(HttpServletResponse response) throws IOException;

    ResultDTO saveEmp(EmpRequestDTO requestDTO);

    ResultDTO uploadEmpExcel(MultipartFile file) throws IOException;

    ResultDTO updateEmp(Long id, EmpUpdateRequestDTO requestDTO);

    void updateHourlyWage(Long id, int hourlyWage);

    ResultDTO deleteEmp(Long id);

    ResultDTO delSelectedEmp(List<Long> id);
}
