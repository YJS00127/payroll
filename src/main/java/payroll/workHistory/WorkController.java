package payroll.workHistory;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import payroll.workHistory.dto.WorkRequestDTO;
import payroll.workHistory.dto.WorkResponseDTO;
import payroll.workHistory.dto.WorkResultDTO;
import payroll.workHistory.dto.WorkUpdateRequestDTO;

@RestController
@RequestMapping("/api/work-history")
@RequiredArgsConstructor
public class WorkController {

    private final WorkService workService;

    @GetMapping
    public ResponseEntity<Page<WorkResponseDTO>> getWorkAll(@RequestBody WorkRequestDTO requestDTO,
                                                            @PageableDefault(size=10)Pageable pageable){
        return ResponseEntity.ok(workService.findAllByEmpId(requestDTO, pageable));
    }

    @PutMapping("/{id}")
    public WorkResultDTO updateWorkHistory(@RequestBody @Valid WorkUpdateRequestDTO updateRequestDTO){
        return workService.updateWorkHistory(updateRequestDTO);
    }

    @DeleteMapping("/{id}")
    public WorkResultDTO deleteWorkHistory(Long id){
        return workService.deleteWorkHistory(id);
    }
}
