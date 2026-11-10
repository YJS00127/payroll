package payroll.leave;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import payroll.common.dto.ResultDTO;
import payroll.leave.dto.LeaveRequestDTO;
import payroll.leave.dto.LeaveResponseDTO;
import payroll.leave.dto.LeaveSearchDTO;

@RestController
@RequestMapping("/api/leave")
@RequiredArgsConstructor
public class LeaveController {

    private final LeaveService leaveService;

    @GetMapping
    public ResponseEntity<Page<LeaveResponseDTO>> getAllLeaves(@RequestBody LeaveSearchDTO searchDTO,
                                                               @PageableDefault(size=10)Pageable pageable){
        return ResponseEntity.ok(leaveService.findAllLeaves(searchDTO, pageable));
    }

    @PostMapping
    public ResponseEntity<ResultDTO> insertLeave(@RequestBody @Valid LeaveRequestDTO requestDTO){
        return ResponseEntity.ok(leaveService.insertLeave(requestDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResultDTO> updateLeave(@RequestBody @Valid LeaveRequestDTO requestDTO,
                                                      Long id){
        return ResponseEntity.ok(leaveService.updateLeave(id, requestDTO));
    }

}
