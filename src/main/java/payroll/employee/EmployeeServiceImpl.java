package payroll.employee;

import jakarta.persistence.EntityExistsException;
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
import payroll.employee.dto.*;
import payroll.entity.*;
import payroll.repository.*;
import payroll.salaryHistory.SalaryHistoryService;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService{

    private final SalaryHistoryService salaryHistoryService;

    private final EmployeeRepository employeeRepository;
    private final LeaveRecordRepository leaveRecordRepository;
    private final DepartmentRepository departmentRepository;

    @Override
    public Page<EmpResponseDTO> findAllEmp(EmpSearchDTO searchDTO, Pageable pageable){
        Specification<Employee> spec = createSpec(searchDTO);

        return employeeRepository.findAll(spec, pageable)
                .map(employee -> {
                    String deptCode = departmentRepository.findById(employee.getDeptId())
                            .orElseThrow(() -> new EntityNotFoundException("해당하는 부서를 찾을 수 없습니다."))
                            .getCode();
                    return new EmpResponseDTO(employee, deptCode);
                });
    }

    @Override
    @Transactional
    public ResultDTO createEmp(EmpRequestDTO requestDTO) {
        Long deptId = departmentRepository.findIdByCode(requestDTO.getDeptCode())
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 부서입니다."));
        Employee emp = requestDTO.toEntity(deptId);

        if(employeeRepository.existsByCode(emp.getCode())){
            throw new EntityExistsException("해당 코드의 사원이 이미 존재합니다.");
        }

        Employee saved = employeeRepository.save(emp);

        LeaveRecord leaveRecord = LeaveRecord.builder()
                .empId(saved.getId())
                .year(LocalDate.now().getYear())
                .month(LocalDate.now().getMonthValue())
                .build();

        leaveRecordRepository.save(leaveRecord);

        return new ResultDTO("success", "사원 저장 성공");
    }

    @Override
    @Transactional
    public ResultDTO createEmpAll(List<EmpRequestDTO> requestDTOs){
        if(requestDTOs == null || requestDTOs.isEmpty()){
            throw new IllegalArgumentException("등록할 사원이 입력되지 않았습니다.");
        }

        Set<String> codes = requestDTOs.stream()
                .map(EmpRequestDTO::getCode)
                .collect(Collectors.toSet());

        if(codes.size() != requestDTOs.size()){
            throw new EntityExistsException("중복되는 사원코드가 존재합니다.");
        }

        if(employeeRepository.existsByCodeIn(codes)){
            throw new EntityExistsException("이미 등록된 사원코드가 존재합니다.");
        }

        Set<String> deptCodes = requestDTOs.stream()
                .map(EmpRequestDTO::getDeptCode)
                .collect(Collectors.toSet());

        Map<String, Long> deptMap = departmentRepository.findByCodeIn(deptCodes).stream()
                .collect(Collectors.toMap(
                        Department::getCode,
                        Department::getId
                ));

        if(deptCodes.size() != deptMap.size()){
            throw new EntityNotFoundException("존재하지 않는 부서가 포함되어있습니다.");
        }

        List<Employee> employees = requestDTOs.stream()
                .map(dto -> dto.toEntity(deptMap.get(dto.getDeptCode())))
                .toList();

        List<Employee> saved = employeeRepository.saveAll(employees);

        List<LeaveRecord> leaves = saved.stream()
                .map(emp -> LeaveRecord.builder()
                        .empId(emp.getId())
                        .year(LocalDate.now().getYear())
                        .month(LocalDate.now().getMonthValue())
                        .build())
                .toList();

        leaveRecordRepository.saveAll(leaves);

        return new ResultDTO("success", "사원 다중 등록 성공");
    }

    @Override
    @Transactional
    public ResultDTO updateEmp(EmpUpdateRequestDTO requestDTO){
        Long deptId = departmentRepository.findIdByCode(requestDTO.getDeptCode())
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 부서입니다."));

        Employee emp = employeeRepository.findByCode(requestDTO.getCode())
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 사원입니다."));
        // 변경 전, 후 급여
        int previousHourlyWage = emp.getHourlyWage();
        int newHourlyWage = requestDTO.getHourlyWage();

        if(previousHourlyWage != newHourlyWage){
            String reason = requestDTO.getReason();
            LocalDate effectiveDate = requestDTO.getEffectiveDate();
            if(reason == null || reason.isBlank()){
                throw new IllegalArgumentException("급여 변경 시 변경 사유는 필수 입력값입니다.");
            }
            if(effectiveDate == null){
                throw new IllegalArgumentException("급여 변경 시 변경 급여 반영일은 필수 입력값입니다.");
            }

            salaryHistoryService.saveSalaryHistory(
                    emp.getId(),
                    previousHourlyWage,
                    newHourlyWage,
                    requestDTO.getEffectiveDate(),
                    requestDTO.getReason()
            );
        }
        emp.update(requestDTO, deptId);

        return new ResultDTO("success", "사원 수정 성공");
    }

    @Override
    @Transactional
    public ResultDTO deleteEmp(Long id){
        Employee emp = employeeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 사원입니다."));

        emp.delete();

        return new ResultDTO("success", "사원 삭제 성공");
    }

    @Override
    @Transactional
    public ResultDTO deleteEmpAll(List<Long> id){
        List<Employee> employees = employeeRepository.findAllById(id);

        if(employees.size() != Set.copyOf(id).size()
        || employees.stream().allMatch(emp -> emp.getDeleteYn().equals("Y"))){
            throw new EntityNotFoundException("존재하지 않는 사원이 포함되어있습니다.");
        }

        employees.forEach(Employee::delete);

        return new ResultDTO("success", "사원 다중 삭제 성공");
    }

    private Specification<Employee> createSpec(EmpSearchDTO searchDTO){
        return(root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.equal(root.get("deleteYn"), "N"));

            if(StringUtils.hasText(searchDTO.getDeptCode())){
                Long deptId = departmentRepository.findIdByCode(searchDTO.getDeptCode())
                        .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 사원입니다."));
                predicates.add(cb.equal(root.get("deptId"), deptId));
            }

            if(StringUtils.hasText(searchDTO.getName())){
                predicates.add(cb.like(
                        root.get("name"),
                        "%" + searchDTO.getName() + "%"
                ));
            }

            if(StringUtils.hasText(searchDTO.getCode())){
                predicates.add(cb.like(
                        root.get("code"),
                        "%" + searchDTO.getCode() + "%"
                ));
            }

            if(StringUtils.hasText(searchDTO.getRank())){
                predicates.add(cb.like(
                        root.get("rank"),
                        "%" + searchDTO.getRank() + "%"
                ));
            }

            if(searchDTO.getJoiningDate() != null){
                predicates.add(cb.equal(root.get("joiningDate"), searchDTO.getJoiningDate()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
