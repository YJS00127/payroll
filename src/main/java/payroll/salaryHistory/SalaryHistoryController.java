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
@RequestMapping("/api")
@RequiredArgsConstructor
public class SalaryHistoryController {
    private final SalaryHistoryService salaryHistoryService;

    @GetMapping("/salary")
    public ResponseEntity<Page<SalaryHistoryResponseDTO>> findSalaryHistory(@ModelAttribute SalaryHistorySearchDTO searchDTO,
                                                            @PageableDefault(size=10) Pageable pageable){
        return ResponseEntity.ok(salaryHistoryService.findSalaryHistory(searchDTO, pageable));
    }

    @GetMapping("/salary/{id}")
    public ResponseEntity<SalaryHistoryResponseDTO> findSalaryHistoryById(@PathVariable Long id){
        return ResponseEntity.ok(salaryHistoryService.findSalaryHistoryById(id));
    }

    @PostMapping("/salary")
    public ResponseEntity<ResultDTO> saveSalaryHistory(@RequestBody SalaryHistoryRequestDTO requestDTO){
        return ResponseEntity.ok(salaryHistoryService.saveSalaryHistory(requestDTO));
    }

    @PutMapping("/salary/{id}")
    public ResponseEntity<ResultDTO> updateSalaryHistory(@PathVariable Long id, @RequestBody SalaryHistoryRequestDTO requestDTO){
        return ResponseEntity.ok(salaryHistoryService.updateSalaryHistory(id, requestDTO));
    }

//    @DeleteMapping("/{id}")
//    public ResponseEntity<ResultDTO> deleteSalaryHistory(Long id){
//        return ResponseEntity.ok(salaryHistoryService.deleteSalaryHistory(id));
//    }
}
