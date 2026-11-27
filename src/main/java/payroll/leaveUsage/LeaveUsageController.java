package payroll.leaveUsage;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import payroll.common.dto.ResultDTO;
import payroll.leaveUsage.dto.LeaveUsageRequestDTO;
import payroll.leaveUsage.dto.LeaveUsageResponseDTO;
import payroll.leaveUsage.dto.LeaveUsageSearchDTO;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

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

    @GetMapping("/excel")
    public void downloadLeaveUsageExcel(HttpServletResponse response) throws IOException{
        response.setHeader(
                HttpHeaders.CONTENT_DISPOSITION,
                ContentDisposition.attachment()
                        .filename("휴가 엑셀 등록 양식.xlsx", StandardCharsets.UTF_8)
                        .build().toString()
        );
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

        leaveUsageService.downloadLeaveUsageExcel(response);
    }

    @PostMapping
    public ResponseEntity<ResultDTO> createLeaveUsage(@RequestBody LeaveUsageRequestDTO requestDTO){
        return ResponseEntity.ok(leaveUsageService.saveLeaveUsage(requestDTO));
    }

    @PostMapping("/excel")
    @Transactional
    public ResponseEntity<ResultDTO> uploadLeaveUsageExcel(MultipartFile file) throws IOException{
        return ResponseEntity.ok(leaveUsageService.uploadLeaveUsageExcel(file));
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

    @DeleteMapping
    public ResponseEntity<ResultDTO> deleteSelectedLeaveUsage(List<Long> ids){
        return ResponseEntity.ok(leaveUsageService.deleteSelectedLeaveUsage(ids));
    }
}
