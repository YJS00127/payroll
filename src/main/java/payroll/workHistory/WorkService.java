package payroll.workHistory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import payroll.common.dto.ResultDTO;
import payroll.entity.AttendanceRecord;
import payroll.workHistory.dto.WorkRequestDTO;
import payroll.workHistory.dto.WorkResponseDTO;
import payroll.workHistory.dto.WorkUpdateRequestDTO;

public interface WorkService {
    Page<WorkResponseDTO> findAllByEmpId(WorkRequestDTO requestDTO, Pageable pageable);

    void saveWorkHistory(WorkRequestDTO requestDTO);

    ResultDTO updateWorkHistory(WorkUpdateRequestDTO updateRequestDTO);

    void autoUpdateWorkHistory(AttendanceRecord attendanceRecord);

//    ResultDTO deleteWorkHistory(Long id);
}
