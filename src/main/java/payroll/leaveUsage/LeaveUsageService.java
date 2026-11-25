package payroll.leaveUsage;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import payroll.common.dto.ResultDTO;
import payroll.leaveUsage.dto.LeaveUsageRequestDTO;
import payroll.leaveUsage.dto.LeaveUsageResponseDTO;
import payroll.leaveUsage.dto.LeaveUsageSearchDTO;

import java.util.List;

public interface LeaveUsageService {
    Page<LeaveUsageResponseDTO> findLeaveUsage(LeaveUsageSearchDTO searchDTO, Pageable pageable);

    LeaveUsageResponseDTO findLeaveUsageById(Long id);

    ResultDTO saveLeaveUsage(LeaveUsageRequestDTO requestDTO);

    ResultDTO updateLeaveUsage(Long id, LeaveUsageRequestDTO requestDTO);

    ResultDTO deleteLeaveUsage(Long id);

    ResultDTO deleteSelectedLeaveUsage(List<Long> ids);
}
