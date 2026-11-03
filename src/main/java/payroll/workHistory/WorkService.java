package payroll.workHistory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import payroll.entity.AttendanceRecord;
import payroll.workHistory.dto.WorkRequestDTO;
import payroll.workHistory.dto.WorkResponseDTO;
import payroll.workHistory.dto.WorkResultDTO;
import payroll.workHistory.dto.WorkUpdateRequestDTO;

public interface WorkService {
    Page<WorkResponseDTO> findAllByEmpId(WorkRequestDTO requestDTO, Pageable pageable);

    void saveWorkHistory(WorkRequestDTO requestDTO);

    WorkResultDTO updateWorkHistory(WorkUpdateRequestDTO updateRequestDTO);

    void autoUpdateWorkHistory(AttendanceRecord attendanceRecord);

    WorkResultDTO deleteWorkHistory(Long id);
}
