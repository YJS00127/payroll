package payroll.salaryHistory;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import payroll.common.dto.ResultDTO;
import payroll.salaryHistory.dto.SalaryHistoryRequestDTO;
import payroll.salaryHistory.dto.SalaryHistoryResponseDTO;
import payroll.salaryHistory.dto.SalaryHistorySearchDTO;

@RestController
@RequestMapping("/api/salary")
@RequiredArgsConstructor
public class SalaryHistoryController {
    private final SalaryHistoryService salaryHistoryService;

    @GetMapping
    public ResponseEntity<Page<SalaryHistoryResponseDTO>> getSalaryHistory(@ModelAttribute SalaryHistorySearchDTO searchDTO,
                                                            @PageableDefault(size=10) Pageable pageable){
        return ResponseEntity.ok(salaryHistoryService.findSalaryHistory(searchDTO, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SalaryHistoryResponseDTO> getSalaryHistoryById(@PathVariable Long id){
        return ResponseEntity.ok(salaryHistoryService.findSalaryHistoryById(id));
    }

    @PostMapping
    public ResponseEntity<ResultDTO> saveSalaryHistory(@RequestBody SalaryHistoryRequestDTO requestDTO){
        return ResponseEntity.ok(salaryHistoryService.saveSalaryHistory(requestDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResultDTO> updateSalaryHistory(@PathVariable Long id, @RequestBody SalaryHistoryRequestDTO requestDTO){
        return ResponseEntity.ok(salaryHistoryService.updateSalaryHistory(id, requestDTO));
    }
}
