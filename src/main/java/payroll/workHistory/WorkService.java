package payroll.workHistory;

import payroll.common.dto.ResultDTO;
import payroll.entity.AttendanceRecord;
import payroll.workHistory.dto.WorkRequestDTO;
import payroll.workHistory.dto.WorkResponseDTO;
import payroll.workHistory.dto.WorkSearchDTO;
import payroll.workHistory.dto.WorkUpdateRequestDTO;

import java.util.List;

public interface WorkService {
    List<WorkResponseDTO> findWork(WorkSearchDTO searchDTO);

    void autoSaveWork(AttendanceRecord attendanceRecord);

    void autoUpdateWorkHistory(Long empId, int year, int month);
}
