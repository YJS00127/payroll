package payroll.department;

import org.springframework.web.bind.annotation.RequestParam;
import payroll.department.dto.DeptCreateRequestDTO;

public interface DepartmentService {

    void insertDept(DeptCreateRequestDTO deptCreateRequestDTO);
}
