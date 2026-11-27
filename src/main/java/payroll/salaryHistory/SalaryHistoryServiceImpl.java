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
import payroll.common.dto.ResultDTO;
import payroll.employee.EmployeeService;
import payroll.entity.Employee;
import payroll.entity.SalaryHistory;
import payroll.repository.EmployeeRepository;
import payroll.repository.SalaryHistoryRepository;
import payroll.salaryHistory.dto.SalaryHistoryRequestDTO;
import payroll.salaryHistory.dto.SalaryHistoryResponseDTO;
import payroll.salaryHistory.dto.SalaryHistorySearchDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class SalaryHistoryServiceImpl implements SalaryHistoryService{
    private final SalaryHistoryRepository salaryHistoryRepository;
    private final EmployeeRepository employeeRepository;
    private final EmployeeService employeeService;

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
    public SalaryHistoryResponseDTO findSalaryHistoryById(Long id){
        SalaryHistory salary = salaryHistoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 급여 기록입니다"));

        Employee emp = employeeRepository.findById(salary.getEmpId())
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 사원입니다"));

        return new SalaryHistoryResponseDTO(salary, emp.getCode(), emp.getName());
    }

    @Override
    @Transactional
    public ResultDTO saveSalaryHistory(SalaryHistoryRequestDTO requestDTO) {
        if(requestDTO.getPreviousHourlyWage() == requestDTO.getNewHourlyWage()){
            throw new IllegalArgumentException("변경될 급여를 다시 입력해주세요");
        }

        if(requestDTO.getReason() == null || requestDTO.getReason().isEmpty()){
            throw new IllegalArgumentException("급여 변경 사유를 입력해주세요");
        }

        SalaryHistory salaryHistory = requestDTO.toEntity();
        salaryHistoryRepository.save(salaryHistory);

        employeeService.updateHourlyWage(requestDTO.getEmpId(), requestDTO.getNewHourlyWage());
        return new ResultDTO("success", "급여 변경 성공");
    }

    @Override
    @Transactional
    public ResultDTO updateSalaryHistory(Long id, SalaryHistoryRequestDTO requestDTO){
        SalaryHistory salaryHistory = salaryHistoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 급여 변경 기록입니다."));

        salaryHistory.update(requestDTO);

        return new ResultDTO("success", "급여 변경 성공");
    }

    @Override
    @Transactional
    public ResultDTO deleteSalaryHistory(Long id){
        SalaryHistory salaryHistory = salaryHistoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 급여 변경 기록입니다."));

        salaryHistory.delete();

        return new ResultDTO("success", "급여 변경 기록 삭제 성공");
    }

    @Override
    @Transactional
    public ResultDTO delSelectedSalaryHistory(List<Long> ids){
        List<SalaryHistory> salaryHistories = salaryHistoryRepository.findAllById(ids);

        if(salaryHistories.size() != Set.copyOf(ids).size() ||
                salaryHistories.stream().anyMatch(salary ->
                    salary.getDeleteYn().equals("Y"))){
            throw new IllegalArgumentException("존재하지 않는 급여 기록이 포함되어있습니다");
        }

        salaryHistories.forEach(SalaryHistory::delete);

        return new ResultDTO("success", "급여 변경 기록 삭제 성공");
    }


    private Specification<SalaryHistory> createSpec(SalaryHistorySearchDTO searchDTO){
        return(root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.equal(root.get("deleteYn"), "N"));

            if(StringUtils.hasText(searchDTO.getKeyword())){
                String keyword = searchDTO.getKeyword().trim();
                Long empId = employeeRepository.findIdByNameOrCode(keyword)
                        .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 사원입니다"));
                predicates.add(cb.or(cb.equal(root.get("empId"), empId)));
            }

            if (searchDTO.getYear() != null) {
                predicates.add(cb.equal(cb.function("YEAR", Integer.class, root.get("effectiveDate")), searchDTO.getYear()));
            }
            if (searchDTO.getMonth() != null) {
                predicates.add(cb.equal(cb.function("MONTH", Integer.class, root.get("effectiveDate")), searchDTO.getMonth()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
