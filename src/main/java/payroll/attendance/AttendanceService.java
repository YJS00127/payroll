package payroll.attendance;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;
import payroll.attendance.dto.AttendanceRequestDTO;
import payroll.attendance.dto.AttendanceResponseDTO;
import payroll.attendance.dto.AttendanceSearchDTO;
import payroll.common.dto.ResultDTO;

import java.io.IOException;
import java.util.List;

public interface AttendanceService {

    Page<AttendanceResponseDTO> getAttendance(AttendanceSearchDTO searchDTO,
                                               Pageable pageable);

    AttendanceResponseDTO getAttendanceById(Long id);

    void downloadAttendanceExcel(HttpServletResponse response) throws IOException;

    ResultDTO saveAttendance(AttendanceRequestDTO requestDTO);

    ResultDTO uploadAttendanceExcel(MultipartFile file) throws IOException;

    ResultDTO updateAttendance(Long id, AttendanceRequestDTO requestDTO);

    ResultDTO deleteAttendance(Long id);

    ResultDTO deleteAttendanceAll(List<Long> id);

}
