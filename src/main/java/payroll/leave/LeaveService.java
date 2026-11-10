package payroll.leave;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import payroll.common.dto.ResultDTO;
import payroll.leave.dto.LeaveRequestDTO;
import payroll.leave.dto.LeaveResponseDTO;
import payroll.leave.dto.LeaveSearchDTO;

public interface LeaveService {
    Page<LeaveResponseDTO> findAllLeaves(LeaveSearchDTO searchDTO, Pageable pageable);

    ResultDTO insertLeave(LeaveRequestDTO requestDTO);

    ResultDTO updateLeave(Long id, LeaveRequestDTO requestDTO);
}
