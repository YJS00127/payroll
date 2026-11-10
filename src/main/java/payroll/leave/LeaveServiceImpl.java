package payroll.leave;

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
import payroll.entity.Employee;
import payroll.entity.LeaveRecord;
import payroll.leave.dto.LeaveRequestDTO;
import payroll.leave.dto.LeaveResponseDTO;
import payroll.leave.dto.LeaveSearchDTO;
import payroll.repository.EmployeeRepository;
import payroll.repository.LeaveRecordRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LeaveServiceImpl implements LeaveService{

    private final LeaveRecordRepository leaveRecordRepository;
    private final EmployeeRepository employeeRepository;

    @Override
    public Page<LeaveResponseDTO> findAllLeaves(LeaveSearchDTO searchDTO,
                                                Pageable pageable){
        Specification<LeaveRecord> spec = createSpec(searchDTO);

        return leaveRecordRepository.findAll(spec, pageable)
                .map(leaveRecord -> {
                    Employee emp = employeeRepository.findById(leaveRecord.getEmpId())
                            .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 사원입니다."));
                    String empCode = emp.getCode();
                    String empName = emp.getName();

                    return new LeaveResponseDTO(leaveRecord, empCode, empName);
                });
    }

    @Override
    @Transactional
    public ResultDTO insertLeave(LeaveRequestDTO requestDTO){
        Long empId = employeeRepository.findIdByCode(requestDTO.getEmpCode())
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 사원입니다."));

        LeaveRecord leaveRecord = requestDTO.toEntity(empId);

        leaveRecordRepository.save(leaveRecord);

        return new ResultDTO("success", "휴가 정보 저장 성공");
    }

    @Override
    @Transactional
    public ResultDTO updateLeave(Long id, LeaveRequestDTO requestDTO){
        LeaveRecord leaveRecord = leaveRecordRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("사원에 대한 휴가 정보가 존재하지 않습니다."));

        leaveRecord.update(requestDTO);

        return new ResultDTO("success", "휴가 정보 수정 성공");
    }


    public Specification<LeaveRecord> createSpec(LeaveSearchDTO searchDTO){
        return(root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if(StringUtils.hasText(searchDTO.getEmpCode())){
                Long empId = employeeRepository.findIdByCode(searchDTO.getEmpCode())
                        .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 사원입니다."));
                predicates.add(cb.equal(root.get("empId"), empId));
            }
            if(searchDTO.getYear() != null){
                predicates.add(cb.equal(root.get("year"), searchDTO.getYear()));
            }
            if(searchDTO.getMonth() != null){
                predicates.add(cb.equal(root.get("month"), searchDTO.getMonth()));
            }
            if(searchDTO.getRemainingLeave() != null){
                predicates.add(cb.equal(root.get("remainingLeave"), searchDTO.getRemainingLeave()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
