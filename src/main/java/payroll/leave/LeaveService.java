package payroll.leave;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import payroll.leave.dto.LeaveRequestDTO;
import payroll.leave.dto.LeaveResponseDTO;
import payroll.leave.dto.LeaveResultDTO;
import payroll.leave.dto.LeaveSearchDTO;

public interface LeaveService {
    Page<LeaveResponseDTO> findAllLeaves(LeaveSearchDTO searchDTO, Pageable pageable);

    LeaveResultDTO insertLeave(LeaveRequestDTO requestDTO);

    LeaveResultDTO updateLeave(Long id, LeaveRequestDTO requestDTO);
}
