package payroll.insuranceRate;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import payroll.insuranceRate.dto.InsuranceRateRequestDTO;
import payroll.insuranceRate.dto.InsuranceRateResponseDTO;
import payroll.insuranceRate.dto.InsuranceRateSearchDTO;
import payroll.common.dto.ResultDTO;
import payroll.entity.InsuranceRateHistory;
import payroll.repository.InsuranceRateHistoryRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class InsuranceRateServiceImpl implements InsuranceRateService{
    private final InsuranceRateHistoryRepository insuranceRateHistoryRepository;

    @Override
    public List<InsuranceRateResponseDTO> findInsuranceRate(InsuranceRateSearchDTO searchDTO){
        Specification<InsuranceRateHistory> spec = createSpec(searchDTO);

        return insuranceRateHistoryRepository.findAll(spec).stream()
                .map(InsuranceRateResponseDTO::new)
                .toList();
    }

    @Override
    public InsuranceRateResponseDTO findInsuranceRateById(Long id){
        InsuranceRateHistory insurance = insuranceRateHistoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 공제율 정보입니다"));
        return new InsuranceRateResponseDTO(insurance);
    }

    @Override
    @Transactional
    public ResultDTO saveInsuranceRate(InsuranceRateRequestDTO requestDTO){
        LocalDate effectiveDate = LocalDate.of(requestDTO.getYear(), requestDTO.getMonth(), 1);
        if(insuranceRateHistoryRepository.existsByEffectiveDate(effectiveDate)){
            throw new EntityExistsException("이미 반영 공제율이 존재합니다");
        }

        InsuranceRateHistory newInsuranceRate = requestDTO.toEntity();

        InsuranceRateHistory previousInsuranceRate = insuranceRateHistoryRepository.findRecentInsuranceRate()
                .orElse(null);

        if(previousInsuranceRate != null && isSame(previousInsuranceRate, newInsuranceRate)){
            throw new IllegalArgumentException("이전 공제율과 일치합니다");
        }

        insuranceRateHistoryRepository.save(newInsuranceRate);

        return new ResultDTO("success", "공제율 등록 성공");
    }

    @Override
    @Transactional
    public ResultDTO updateInsuranceRate(Long id,InsuranceRateRequestDTO requestDTO){
        InsuranceRateHistory previousInsuranceRate = insuranceRateHistoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 공제율 정보입니다"));
        InsuranceRateHistory newInsuranceRate = requestDTO.toEntity();

        if(isSame(previousInsuranceRate, newInsuranceRate) &&
        previousInsuranceRate.getEffectiveDate().equals(newInsuranceRate.getEffectiveDate())){
            throw new IllegalArgumentException("이전 공제율과 일치합니다");
        }
        previousInsuranceRate.update(requestDTO);

        return new ResultDTO("success", "공제율 수정 성공");
    }

    @Override
    @Transactional
    public ResultDTO deleteInsuranceRate(Long id){
        InsuranceRateHistory insurance = insuranceRateHistoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 공제율 정보입니다"));

        insuranceRateHistoryRepository.delete(insurance);

        return new ResultDTO("success", "공제율 삭제 성공");
    }

    @Override
    @Transactional
    public ResultDTO deleteInsuranceRates(List<Long> ids){
        List<InsuranceRateHistory> insurances = insuranceRateHistoryRepository.findAllById(ids);

        if(insurances.size() != Set.copyOf(ids).size()){
            throw new IllegalArgumentException("존재하지 않는 공제율 정보가 포함되어있습니다");
        }

        insuranceRateHistoryRepository.deleteAll(insurances);

        return new ResultDTO("success", "공제율 삭제 성공");
    }


    private Specification<InsuranceRateHistory> createSpec(InsuranceRateSearchDTO searchDTO){
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if(searchDTO.getYear() != null){
                predicates.add(cb.equal(
                        cb.function("YEAR", Integer.class, root.get("effectiveDate")), searchDTO.getYear()));
            }

            if(searchDTO.getMonth() != null){
                predicates.add(cb.equal(
                        cb.function("MONTH", Integer.class, root.get("effectiveDate")), searchDTO.getMonth()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }


    private boolean isSame(InsuranceRateHistory previousInsurance, InsuranceRateHistory newInsurance){
        return newInsurance.getNationalPensionRate().compareTo(previousInsurance.getNationalPensionRate()) == 0
                && newInsurance.getHealthInsuranceRate().compareTo(previousInsurance.getHealthInsuranceRate()) == 0
                && newInsurance.getLongTermCareInsuranceRate().compareTo(previousInsurance.getLongTermCareInsuranceRate()) == 0
                && newInsurance.getEmploymentInsuranceRate().compareTo(previousInsurance.getEmploymentInsuranceRate()) == 0
                && newInsurance.getLocalIncomeTaxRate().compareTo(previousInsurance.getLocalIncomeTaxRate()) == 0;
    }
}
