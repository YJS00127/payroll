package payroll.attendance;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import payroll.attendance.dto.AttendanceRequestDTO;
import payroll.attendance.dto.AttendanceResponseDTO;
import payroll.attendance.dto.AttendanceSearchDTO;
import payroll.common.dto.ResultDTO;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/attendance")
public class AttendanceController {

    private final AttendanceService attendanceService;

    @GetMapping
    public ResponseEntity<Page<AttendanceResponseDTO>> getAttendance(@ModelAttribute @Valid AttendanceSearchDTO searchDTO,
                                                                     @PageableDefault(size=10, sort = {"workDate", "id"})Pageable pageable){
        return ResponseEntity.ok(attendanceService.getAttendance(searchDTO, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AttendanceResponseDTO> getAttendanceById(@PathVariable Long id){
        return ResponseEntity.ok(attendanceService.getAttendanceById(id));
    }

    @GetMapping("/excel")
    public void downloadAttendanceExcel(HttpServletResponse response) throws IOException{
        response.setHeader(
                HttpHeaders.CONTENT_DISPOSITION,
                ContentDisposition.attachment()
                        .filename("근태 엑셀 등록 양식.xlsx", StandardCharsets.UTF_8)
                        .build().toString()
        );
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

        attendanceService.downloadAttendanceExcel(response);
    }

    @PostMapping
    public ResponseEntity<ResultDTO> createAttendance(@RequestBody @Valid AttendanceRequestDTO requestDTO){
        return ResponseEntity.ok(attendanceService.saveAttendance(requestDTO));
    }

    @PostMapping("/excel")
    public ResponseEntity<ResultDTO> uploadAttendanceExcel(MultipartFile file) throws IOException{
        return ResponseEntity.ok(attendanceService.uploadAttendanceExcel(file));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResultDTO> updateAttendance(@PathVariable Long id,
                                                      @RequestBody @Valid AttendanceRequestDTO requestDTO){
        return ResponseEntity.ok(attendanceService.updateAttendance(id, requestDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResultDTO> deleteAttendance(@PathVariable Long id){
        return ResponseEntity.ok(attendanceService.deleteAttendance(id));
    }

    @DeleteMapping()
    public ResponseEntity<ResultDTO> deleteSelectedAttendance(@RequestBody List<Long> ids){
        return ResponseEntity.ok(attendanceService.deleteAttendanceAll(ids));
    }

}
