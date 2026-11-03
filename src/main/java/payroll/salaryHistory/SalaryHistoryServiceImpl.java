package payroll.salaryHistory;

import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import payroll.entity.Employee;
import payroll.entity.SalaryHistory;
import payroll.repository.EmployeeRepository;
import payroll.repository.SalaryHistoryRepository;
import payroll.salaryHistory.dto.SalaryHistoryResponseDTO;
import payroll.salaryHistory.dto.SalaryHistoryResultDTO;
import payroll.salaryHistory.dto.SalaryHistorySearchDTO;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class SalaryHistoryServiceImpl implements SalaryHistoryService{
    private final SalaryHistoryRepository salaryHistoryRepository;
    private final EmployeeRepository employeeRepository;

    @Override
    public Page<SalaryHistoryResponseDTO> findSalaryHistory(SalaryHistorySearchDTO searchDTO,
                                                            Pageable pageable){
        Specification<SalaryHistory> spec = createSpec(searchDTO);

        return salaryHistoryRepository.findAll(spec, pageable)
                .map(salaryHistory -> {
                    Employee emp = employeeRepository.findById(salaryHistory.getEmpId())
                            .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 사원입니다."));

                    String empCode = emp.getCode();
                    String empName = emp.getName();

                    return new SalaryHistoryResponseDTO(salaryHistory, empCode, empName);
                });
    }

    @Override
    @Transactional
    public SalaryHistoryResultDTO saveSalaryHistory(Long empId, int previousHourlyWage, int newHourlyWage,
                                                    LocalDate effectiveDate, String reason) {
        SalaryHistory salaryHistory = SalaryHistory.builder()
                .empId(empId)
                .previousHourlyWage(previousHourlyWage)
                .newHourlyWage(newHourlyWage)
                .effectiveDate(effectiveDate)
                .reason(reason)
                .build();

        salaryHistoryRepository.save(salaryHistory);

        return new SalaryHistoryResultDTO("success", "급여 변경 이력 저장 성공");
    }
    @Override
    @Transactional
    public SalaryHistoryResultDTO deleteSalaryHistory(Long id){
        SalaryHistory salaryHistory = salaryHistoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 급여 기록 정보입니다."));

        salaryHistory.delete();

        return new SalaryHistoryResultDTO("success", "급여 기록 삭제 성공");
    }



    private Specification<SalaryHistory> createSpec(SalaryHistorySearchDTO searchDTO){
        return(root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.equal(root.get("deleteYn"), 'N'));

            if(StringUtils.hasText(searchDTO.getEmpCode())){
                predicates.add(cb.like(
                        root.get("empCode"),
                        "%" + searchDTO.getEmpCode() + "%"
                ));
            }

            if(searchDTO.getYear() != null){
                predicates.add(cb.equal(root.get("year"), searchDTO.getYear()));
            }

            if(searchDTO.getMonth() != null){
                predicates.add(cb.equal(root.get("month"), searchDTO.getMonth()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

}
