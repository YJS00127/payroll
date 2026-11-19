package payroll.leaveUsage;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import payroll.common.dto.ResultDTO;
import payroll.leaveUsage.dto.LeaveUsageRequestDTO;
import payroll.leaveUsage.dto.LeaveUsageResponseDTO;
import payroll.leaveUsage.dto.LeaveUsageSearchDTO;

@RestController
@RequestMapping("/api/leave-usage")
@RequiredArgsConstructor
public class LeaveUsageController {
    private final LeaveUsageService leaveUsageService;

    @GetMapping
    public ResponseEntity<Page<LeaveUsageResponseDTO>> getLeaveUsageAll(@ModelAttribute LeaveUsageSearchDTO searchDTO,
                                                                        @PageableDefault(size=10)Pageable pageable){
        return ResponseEntity.ok(leaveUsageService.findLeaveUsage(searchDTO, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<LeaveUsageResponseDTO> getLeaveUsageById(@PathVariable Long id){
        return ResponseEntity.ok(leaveUsageService.findLeaveUsageById(id));
    }

    @PostMapping
    public ResponseEntity<ResultDTO> createLeaveUsage(@RequestBody LeaveUsageRequestDTO requestDTO){
        return ResponseEntity.ok(leaveUsageService.saveLeaveUsage(requestDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResultDTO> updateLeaveUsage(@PathVariable Long id,
                                                      @RequestBody LeaveUsageRequestDTO requestDTO){
        return ResponseEntity.ok(leaveUsageService.updateLeaveUsage(id, requestDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResultDTO> deleteLeaveUsage(@PathVariable Long id){
        return ResponseEntity.ok(leaveUsageService.deleteLeaveUsage(id));
    }

//    @DeleteMapping
//    public ResponseEntity<ResultDTO> de
}
