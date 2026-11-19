package payroll.insuranceRate;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import payroll.insuranceRate.dto.InsuranceRateRequestDTO;
import payroll.insuranceRate.dto.InsuranceRateResponseDTO;
import payroll.insuranceRate.dto.InsuranceRateSearchDTO;
import payroll.common.dto.ResultDTO;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class InsuranceRateController {
    private final InsuranceRateService insuranceRateService;

    @GetMapping("/insurance-rate")
    public ResponseEntity<List<InsuranceRateResponseDTO>> findInsuranceRate(@ModelAttribute @Valid InsuranceRateSearchDTO searchDTO){
        return ResponseEntity.ok(insuranceRateService.findInsuranceRate(searchDTO));
    }

    @GetMapping("/insurance-rate/{id}")
    public ResponseEntity<InsuranceRateResponseDTO> findInsuranceRateById(@PathVariable Long id){
        return ResponseEntity.ok(insuranceRateService.findInsuranceRateById(id));
    }

    @PostMapping("/insurance-rate")
    public ResponseEntity<ResultDTO> insertInsuranceRate(@RequestBody @Valid InsuranceRateRequestDTO requestDTO){
        return ResponseEntity.ok(insuranceRateService.saveInsuranceRate(requestDTO));
    }

    @PutMapping("/insurance-rate/{id}")
    public ResponseEntity<ResultDTO> updateInsuranceRate(@PathVariable Long id,
                                                         @RequestBody @Valid InsuranceRateRequestDTO requestDTO){
        return ResponseEntity.ok(insuranceRateService.updateInsuranceRate(id, requestDTO));
    }

    @DeleteMapping("/insurance-rate/{id}")
    public ResponseEntity<ResultDTO> deleteInsuranceRate(@PathVariable Long id){
        return ResponseEntity.ok(insuranceRateService.deleteInsuranceRate((id)));
    }

    @DeleteMapping("/insurance-rate")
    public ResponseEntity<ResultDTO> deleteInsuranceRates(@RequestBody List<Long> ids){
        return ResponseEntity.ok(insuranceRateService.deleteInsuranceRates(ids));
    }
}
