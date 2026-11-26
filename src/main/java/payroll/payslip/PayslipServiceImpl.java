package payroll.payslip;

import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.Predicate;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import payroll.common.dto.ResultDTO;
import payroll.entity.Employee;
import payroll.entity.PayrollHistory;
import payroll.payslip.dto.PayslipDateRequestDTO;
import payroll.payslip.dto.PayslipResponseDTO;
import payroll.payslip.dto.PayslipSearchDTO;
import payroll.repository.EmployeeRepository;
import payroll.repository.PayrollHistoryRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class PayslipServiceImpl implements PayslipService{
    private final EmployeeRepository employeeRepository;
    private final PayrollHistoryRepository payrollHistoryRepository;

    @Override
    public Page<PayslipResponseDTO> findPayslip(PayslipSearchDTO searchDTO, Pageable pageable){
        Specification<PayrollHistory> spec = createSpec(searchDTO);

        return payrollHistoryRepository.findAll(spec, pageable)
                .map(payrollHistory -> {
                   Employee emp = employeeRepository.findById(payrollHistory.getId())
                           .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 사원입니다"));

                   return new PayslipResponseDTO(payrollHistory, emp);
                });
    }

    @Override
    @Transactional
    public ResultDTO saveConfirmedAt(PayslipDateRequestDTO requestDTO){
        List<PayrollHistory> payrollHistories = payrollHistoryRepository.findByIds(requestDTO.getIds());

        if(payrollHistories.stream()
                .anyMatch(payroll -> payroll.getConfirmedAt() != null)){
            throw new IllegalArgumentException("이미 확정된 급여 내역이 존재합니다");
        }

        payrollHistories.forEach(payroll -> payroll.confirmPayroll(requestDTO.getDate()));


        return new ResultDTO("success", "급여 확정 완료");
    }

    @Override
    @Transactional
    public ResultDTO savePaymentDate(PayslipDateRequestDTO requestDTO){
        List<PayrollHistory> payrollHistories = payrollHistoryRepository.findByIds(requestDTO.getIds());

        if(payrollHistories.stream()
                .anyMatch(payroll -> payroll.getPaymentDate() != null)){
            throw new IllegalArgumentException("이미 지급된 급여 내역이 존재합니다");
        }

        payrollHistories.forEach(payroll -> payroll.confirmPaymentDate(requestDTO.getDate()));

        return new ResultDTO("success", "급여 지급 완료");
    }

    @Override
    @Transactional
    public ResultDTO deleteConfirmedAt(List<Long> ids){
        List<PayrollHistory> payrollHistories = payrollHistoryRepository.findByIds(ids);

        payrollHistories.forEach(PayrollHistory::cancelConfirmedAt);

        return new ResultDTO("success", "급여 확정 취소");
    }

    private Specification<PayrollHistory> createSpec(PayslipSearchDTO searchDTO){
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            String keyword = searchDTO.getKeyword();
            Employee emp = employeeRepository.findAllByNameOrCode(keyword)
                            .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 사원입니다"));

            predicates.add(cb.equal(root.get("deleteYn"), "N"));

            if(StringUtils.hasText(keyword)){
                Long empId = emp.getId();
                predicates.add(cb.equal(root.get("empId"), empId));
            }

            if(StringUtils.hasText(searchDTO.getDeptName())){
                Long deptId = emp.getDeptId();
                predicates.add(cb.equal(root.get("deptId"), deptId));
            }

            if(StringUtils.hasText(searchDTO.getEmpRank())){
                List<Long> empIds = employeeRepository.findIdsByRank(searchDTO.getEmpRank());
                predicates.add(root.get("empId").in(empIds));
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
