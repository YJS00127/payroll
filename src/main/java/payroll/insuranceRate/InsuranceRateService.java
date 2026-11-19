package payroll.insuranceRate;

import payroll.insuranceRate.dto.InsuranceRateRequestDTO;
import payroll.insuranceRate.dto.InsuranceRateResponseDTO;
import payroll.insuranceRate.dto.InsuranceRateSearchDTO;
import payroll.common.dto.ResultDTO;

import java.util.List;

public interface InsuranceRateService {
    List<InsuranceRateResponseDTO> findInsuranceRate(InsuranceRateSearchDTO searchDTO);

    InsuranceRateResponseDTO findInsuranceRateById(Long Id);

    ResultDTO saveInsuranceRate(InsuranceRateRequestDTO requestDTO);

    ResultDTO updateInsuranceRate(Long id, InsuranceRateRequestDTO requestDTO);

    ResultDTO deleteInsuranceRate(Long id);

    ResultDTO deleteInsuranceRates(List<Long> ids);
}
