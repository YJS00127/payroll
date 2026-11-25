package payroll.leave;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import payroll.common.dto.ResultDTO;
import payroll.leave.dto.LeaveRequestDTO;
import payroll.leave.dto.LeaveResponseDTO;
import payroll.leave.dto.LeaveSearchDTO;
import payroll.leave.dto.LeaveUpdateRequestDTO;

import java.util.List;

@RestController
@RequestMapping("/api/leave-record")
@RequiredArgsConstructor
public class LeaveController {

    private final LeaveService leaveService;

    @GetMapping
    public ResponseEntity<Page<LeaveResponseDTO>> getLeaveAll(@ModelAttribute LeaveSearchDTO searchDTO,
                                                              @PageableDefault(size=10, sort="id", direction = Sort.Direction.DESC)Pageable pageable){
        return ResponseEntity.ok(leaveService.findLeaveAll(searchDTO, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<LeaveResponseDTO> getLeaveBId(@PathVariable Long id){
        return ResponseEntity.ok(leaveService.findLeaveById(id));
    }

    @PostMapping
    public ResponseEntity<ResultDTO> insertLeave(@RequestBody @Valid LeaveRequestDTO requestDTO){
        return ResponseEntity.ok(leaveService.saveLeave(requestDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResultDTO> updateLeave(@RequestBody @Valid LeaveUpdateRequestDTO requestDTO,
                                                 @PathVariable Long id){
        return ResponseEntity.ok(leaveService.updateLeave(id, requestDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResultDTO> deleteLeave(@PathVariable Long id){
        return ResponseEntity.ok(leaveService.deleteLeave(id));
    }

    @DeleteMapping
    public ResponseEntity<ResultDTO> deleteSelectedIds(@RequestBody List<Long> ids){
        return ResponseEntity.ok(leaveService.deleteSelectedLeave(ids));
    }

}
