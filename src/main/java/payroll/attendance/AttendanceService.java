package payroll.attendance;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import payroll.attendance.dto.AttendanceRequestDTO;
import payroll.attendance.dto.AttendanceResponseDTO;
import payroll.attendance.dto.AttendanceResultDTO;
import payroll.attendance.dto.AttendanceSearchDTO;

public interface AttendanceService {

    Page<AttendanceResponseDTO> findAttendance(AttendanceSearchDTO searchDTO,
                                               Pageable pageable);

    AttendanceResultDTO saveAttendance(AttendanceRequestDTO requestDTO);

    AttendanceResultDTO updateAttendance(AttendanceRequestDTO requestDTO);

    AttendanceResultDTO deleteAttendance(Long id);

}
