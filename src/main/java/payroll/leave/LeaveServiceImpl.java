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
import payroll.leave.dto.LeaveUpdateRequestDTO;
import payroll.repository.EmployeeRepository;
import payroll.repository.LeaveRecordRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class LeaveServiceImpl implements LeaveService{

    private final LeaveRecordRepository leaveRecordRepository;
    private final EmployeeRepository employeeRepository;

    @Override
    public Page<LeaveResponseDTO> findLeaveAll(LeaveSearchDTO searchDTO,
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
    public LeaveResponseDTO findLeaveById(Long id){
        LeaveRecord leaveRecord = leaveRecordRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 휴가 정보입니다"));

        Employee emp = employeeRepository.findById(leaveRecord.getEmpId())
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 사원입니다"));

        return new LeaveResponseDTO(leaveRecord, emp.getCode(), emp.getName());
    }

    @Override
    @Transactional
    public ResultDTO saveLeave(LeaveRequestDTO requestDTO){
        Long empId = employeeRepository.findIdByCode(requestDTO.getEmpCode())
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 사원입니다."));

        LeaveRecord leaveRecord = requestDTO.toEntity(empId);

        leaveRecordRepository.save(leaveRecord);

        return new ResultDTO("success", "휴가 정보 저장 성공");
    }

    @Override
    @Transactional
    public ResultDTO updateLeave(Long id, LeaveUpdateRequestDTO requestDTO){
        LeaveRecord leaveRecord = leaveRecordRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("사원에 대한 휴가 정보가 존재하지 않습니다."));

        if(leaveRecord.getAccruedLeave() != requestDTO.getAccruedLeave() &&
                leaveRecord.getTotalLeave() + requestDTO.getAccruedLeave() != requestDTO.getTotalLeave()){
            leaveRecord.update(requestDTO);
        }
        else{
            leaveRecord.update(requestDTO);
        }

        return new ResultDTO("success", "휴가 정보 수정 성공");
    }
// 총 휴가 : 4, 발생휴가: 1  / (수정) 총 휴가 5, 발생휴가: 1 -> 총 휴가 : 6, 발생휴가: 1
// 발생휴가 전과 후가 다를 때만 total에 더하기
    @Override
    @Transactional
    public ResultDTO deleteLeave(Long id){
        LeaveRecord leaveRecord = leaveRecordRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 휴가 정보입니다"));

        leaveRecord.delete();

        return new ResultDTO("success", "휴가 정보 삭제 성공");
    }

    @Override
    @Transactional
    public ResultDTO deleteSelectedLeave(List<Long> ids){
        List<LeaveRecord> leaveRecords = leaveRecordRepository.findAllById(ids);

        if(leaveRecords.size() != Set.copyOf(ids).size() &&
        leaveRecords.stream().allMatch(record -> record.getDeleteYn().equals("Y"))){
            throw new IllegalArgumentException("존재하지 않는 휴가 정보가 포함되어 있습니다");
        }
        leaveRecords.forEach(LeaveRecord::delete);

        return new ResultDTO("success", "휴가 정보 삭제 성공");
    }


    public Specification<LeaveRecord> createSpec(LeaveSearchDTO searchDTO){
        return(root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.equal(root.get("deleteYn"), "N"));

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
