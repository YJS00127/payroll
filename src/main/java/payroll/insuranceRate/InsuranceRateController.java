package payroll.insuranceRate;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import payroll.insuranceRate.dto.InsuranceRateRequestDTO;
import payroll.insuranceRate.dto.InsuranceRateResponseDTO;
import payroll.insuranceRate.dto.InsuranceRateSearchDTO;
import payroll.common.dto.ResultDTO;

@RestController
@RequestMapping("/insurance-rate")
@RequiredArgsConstructor
public class InsuranceRateController {
    private final InsuranceRateService insuranceRateService;

    @GetMapping
    public ResponseEntity<Page<InsuranceRateResponseDTO>> findInsuranceRate(@RequestBody @Valid InsuranceRateSearchDTO searchDTO,
                                                                            Pageable pageable){
        return ResponseEntity.ok(insuranceRateService.findInsuranceRate(searchDTO, pageable));
    }

    @PostMapping
    public ResponseEntity<ResultDTO> insertInsuranceRate(@RequestBody @Valid InsuranceRateRequestDTO requestDTO){
        return ResponseEntity.ok(insuranceRateService.saveInsuranceRate(requestDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResultDTO> updateInsuranceRate(@PathVariable Long id,
                                                         @RequestBody @Valid InsuranceRateRequestDTO requestDTO){
        return ResponseEntity.ok(insuranceRateService.updateInsuranceRate(id, requestDTO));
    }

}
