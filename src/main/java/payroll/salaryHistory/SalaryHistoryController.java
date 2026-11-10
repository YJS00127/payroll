package payroll.salaryHistory;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import payroll.common.dto.ResultDTO;
import payroll.salaryHistory.dto.SalaryHistoryResponseDTO;
import payroll.salaryHistory.dto.SalaryHistorySearchDTO;

@RestController
@RequestMapping("/api/salary-history")
@RequiredArgsConstructor
public class SalaryHistoryController {
    private final SalaryHistoryService salaryHistoryService;

    @GetMapping
    public ResponseEntity<Page<SalaryHistoryResponseDTO>> findSalaryHistory(@RequestBody SalaryHistorySearchDTO searchDTO,
                                                            @PageableDefault(size=10)Pageable pageable){
        return ResponseEntity.ok(salaryHistoryService.findSalaryHistory(searchDTO, pageable));
    }

//    @DeleteMapping("/{id}")
//    public ResponseEntity<ResultDTO> deleteSalaryHistory(Long id){
//        return ResponseEntity.ok(salaryHistoryService.deleteSalaryHistory(id));
//    }
}
