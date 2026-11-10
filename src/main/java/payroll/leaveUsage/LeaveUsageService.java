package payroll.leaveUsage;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import payroll.common.dto.ResultDTO;
import payroll.leaveUsage.dto.LeaveUsageRequestDTO;
import payroll.leaveUsage.dto.LeaveUsageResponseDTO;
import payroll.leaveUsage.dto.LeaveUsageSearchDTO;

public interface LeaveUsageService {
    Page<LeaveUsageResponseDTO> findLeaveUsage(LeaveUsageSearchDTO searchDTO, Pageable pageable);

    ResultDTO saveLeaveUsage(LeaveUsageRequestDTO requestDTO);

    ResultDTO updateLeaveUsage(Long id, LeaveUsageRequestDTO requestDTO);

    ResultDTO deleteLeaveUsage(Long id);
}
