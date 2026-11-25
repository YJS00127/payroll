package payroll.attendance;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import payroll.attendance.dto.AttendanceRequestDTO;
import payroll.attendance.dto.AttendanceResponseDTO;
import payroll.attendance.dto.AttendanceSearchDTO;
import payroll.common.dto.ResultDTO;

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

    @PostMapping
    public ResponseEntity<ResultDTO> createAttendance(@RequestBody @Valid AttendanceRequestDTO requestDTO){
        return ResponseEntity.ok(attendanceService.saveAttendance(requestDTO));
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
