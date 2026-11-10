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
import payroll.attendance.dto.AttendanceSearchDTO;
import payroll.common.dto.ResultDTO;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class AttendanceController {

    private final AttendanceService attendanceService;

    @GetMapping("/attendance")
    public ResponseEntity<Page<AttendanceResponseDTO>> getAttendance(@RequestBody AttendanceSearchDTO searchDTO,
                                                                     @PageableDefault(size=10)Pageable pageable){
        return ResponseEntity.ok(attendanceService.findAttendance(searchDTO, pageable));
    }

    @PostMapping("/attendance")
    public ResponseEntity<ResultDTO> createAttendance(@RequestBody @Valid AttendanceRequestDTO requestDTO){
        return ResponseEntity.ok(attendanceService.saveAttendance(requestDTO));
    }

    @PostMapping("/attendances")
    public ResponseEntity<ResultDTO> createAttendances(@RequestBody @Valid List<AttendanceRequestDTO> requestDTO){
        return ResponseEntity.ok(attendanceService.saveAttendanceAll(requestDTO));
    }

    @PutMapping("/attendance/{id}")
    public ResponseEntity<ResultDTO> updateAttendance(@PathVariable Long id,
                                                      @RequestBody @Valid AttendanceRequestDTO requestDTO){
        return ResponseEntity.ok(attendanceService.updateAttendance(id, requestDTO));
    }

    @DeleteMapping("/attendance/{id}")
    public ResponseEntity<ResultDTO> deleteAttendance(Long id){
        return ResponseEntity.ok(attendanceService.deleteAttendance(id));
    }

    @DeleteMapping("/attendances/{id}")
    public ResponseEntity<ResultDTO> deleteAttendanceAll(List<Long> ids){
        return ResponseEntity.ok(attendanceService.deleteAttendanceAll(ids));
    }

}
