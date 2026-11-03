package payroll.attendance;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import payroll.attendance.dto.AttendanceRequestDTO;
import payroll.attendance.dto.AttendanceResponseDTO;
import payroll.attendance.dto.AttendanceResultDTO;
import payroll.attendance.dto.AttendanceSearchDTO;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/attendance")
public class AttendanceController {

    private final AttendanceService attendanceService;

    @GetMapping
    public ResponseEntity<Page<AttendanceResponseDTO>> getAttendance(@RequestBody AttendanceSearchDTO searchDTO,
                                                                     @PageableDefault(size=10)Pageable pageable){
        return ResponseEntity.ok(attendanceService.findAttendance(searchDTO, pageable));
    }

    @PostMapping
    public ResponseEntity<AttendanceResultDTO> createAttendance(@RequestBody @Valid AttendanceRequestDTO requestDTO){
        return ResponseEntity.ok(attendanceService.saveAttendance(requestDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AttendanceResultDTO> updateAttendance(@RequestBody @Valid AttendanceRequestDTO requestDTO){
        return ResponseEntity.ok(attendanceService.updateAttendance(requestDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<AttendanceResultDTO> deleteAttendance(Long id){
        return ResponseEntity.ok(attendanceService.deleteAttendance(id));
    }

}
