package payroll.leaveUsage;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;
import payroll.common.dto.ResultDTO;
import payroll.leaveUsage.dto.LeaveUsageRequestDTO;
import payroll.leaveUsage.dto.LeaveUsageResponseDTO;
import payroll.leaveUsage.dto.LeaveUsageSearchDTO;

import java.io.IOException;
import java.util.List;

public interface LeaveUsageService {
    Page<LeaveUsageResponseDTO> findLeaveUsage(LeaveUsageSearchDTO searchDTO, Pageable pageable);

    LeaveUsageResponseDTO findLeaveUsageById(Long id);

    void downloadLeaveUsageExcel(HttpServletResponse response) throws IOException;

    ResultDTO saveLeaveUsage(LeaveUsageRequestDTO requestDTO);

    ResultDTO uploadLeaveUsageExcel(MultipartFile file) throws IOException;

    ResultDTO updateLeaveUsage(Long id, LeaveUsageRequestDTO requestDTO);

    ResultDTO deleteLeaveUsage(Long id);

    ResultDTO deleteSelectedLeaveUsage(List<Long> ids);
}
