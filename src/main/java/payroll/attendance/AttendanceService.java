package payroll.attendance;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import payroll.attendance.dto.AttendanceRequestDTO;
import payroll.attendance.dto.AttendanceResponseDTO;
import payroll.attendance.dto.AttendanceSearchDTO;
import payroll.common.dto.ResultDTO;

import java.util.List;

public interface AttendanceService {

    Page<AttendanceResponseDTO> getAttendance(AttendanceSearchDTO searchDTO,
                                               Pageable pageable);

    AttendanceResponseDTO getAttendanceById(Long id);

    ResultDTO saveAttendance(AttendanceRequestDTO requestDTO);

    ResultDTO saveAttendanceAll(List<AttendanceRequestDTO> requestDTOs);

    ResultDTO updateAttendance(Long id, AttendanceRequestDTO requestDTO);

    ResultDTO deleteAttendance(Long id);

    ResultDTO deleteAttendanceAll(List<Long> id);

}
