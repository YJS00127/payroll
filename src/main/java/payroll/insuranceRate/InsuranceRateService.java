package payroll.insuranceRate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import payroll.insuranceRate.dto.InsuranceRateRequestDTO;
import payroll.insuranceRate.dto.InsuranceRateResponseDTO;
import payroll.insuranceRate.dto.InsuranceRateSearchDTO;
import payroll.common.dto.ResultDTO;

public interface InsuranceRateService {
    Page<InsuranceRateResponseDTO> findInsuranceRate(InsuranceRateSearchDTO searchDTO, Pageable pageable);

    ResultDTO saveInsuranceRate(InsuranceRateRequestDTO requestDTO);

    ResultDTO updateInsuranceRate(Long id, InsuranceRateRequestDTO requestDTO);
}
