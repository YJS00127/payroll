package payroll.leave;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import payroll.common.dto.ResultDTO;
import payroll.leave.dto.LeaveRequestDTO;
import payroll.leave.dto.LeaveResponseDTO;
import payroll.leave.dto.LeaveSearchDTO;
import payroll.leave.dto.LeaveUpdateRequestDTO;

import java.util.List;

public interface LeaveService {
    Page<LeaveResponseDTO> findLeaveAll(LeaveSearchDTO searchDTO, Pageable pageable);

    LeaveResponseDTO findLeaveById(Long id);

    ResultDTO saveLeave(LeaveRequestDTO requestDTO);

    ResultDTO updateLeave(Long id, LeaveUpdateRequestDTO requestDTO);

    ResultDTO deleteLeave(Long id);

    ResultDTO deleteSelectedLeave(List<Long> ids);
}
