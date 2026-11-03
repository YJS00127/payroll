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
import payroll.employee.dto.*;
import payroll.entity.*;
import payroll.repository.*;
import payroll.salaryHistory.SalaryHistoryService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService{

    private final SalaryHistoryService salaryHistoryService;

    private final EmployeeRepository employeeRepository;
    private final LeaveHistoryRepository leaveRecordRepository;
    private final DepartmentRepository departmentRepository;
//    private final AttendanceRecord attendanceRecord;
//    private final LeaveRecord leaveRecord;
//    private final PayrollHistory payrollHistory;
//    private final SalaryHistory salaryHistory;
//    private final WorkHistory workHistory;

    @Override
    public Page<EmpResponseDTO> findAllEmp(EmpSearchDTO searchDTO, Pageable pageable){
        Specification<Employee> spec = createSpec(searchDTO);

        return employeeRepository.findAll(spec, pageable)
                .map(employee -> {
                    String deptCode = departmentRepository.findById(employee.getId())
                            .orElseThrow(() -> new EntityNotFoundException("해당하는 부서를 찾을 수 없습니다."))
                            .getCode();
                    return new EmpResponseDTO(employee, deptCode);
                });
    }

    @Override
    @Transactional
    public EmpResultDTO createEmp(EmpRequestDTO requestDTO) {
        Long deptId = departmentRepository.findIdByCode(requestDTO.getDeptCode());
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

        if(leaveRecordRepository.existsByEmpIdAndYearAndMonth(
                leaveRecord.getEmpId(),
                leaveRecord.getYear(),
                leaveRecord.getMonth())){
            throw new EntityExistsException("해당 사원의 휴가 정보가 이미 존재합니다.");
        }

        leaveRecordRepository.save(leaveRecord);

        return new EmpResultDTO("success", "사원 저장 성공");
    }

    @Override
    @Transactional
    public EmpResultDTO updateEmp(EmpUpdateRequestDTO requestDTO){
        Long deptId = departmentRepository.findIdByCode(requestDTO.getDeptCode());

        Employee emp = employeeRepository.findByCode(requestDTO.getCode())
                .orElseThrow(() -> new EntityNotFoundException("사원이 존재하지 않습니다."));
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

        return new EmpResultDTO("success", "사원 수정 성공");
    }

    @Override
    @Transactional
    public EmpResultDTO deleteEmp(Long id){
        Employee emp = employeeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("사원이 존재하지 않습니다."));

        emp.delete();

//        attendanceRecord.delete();
//        leaveRecord.delete();
//        payrollHistory.delete();
//        salaryHistory.delete();
//        workHistory.delete();

        return new EmpResultDTO("success", "사원 삭제 성공");
    }

    private Specification<Employee> createSpec(EmpSearchDTO searchDTO){
        return(root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.equal(root.get("deleteYn"), "N"));

            if(StringUtils.hasText(searchDTO.getDeptCode())){
                Long deptId = employeeRepository.findIdByCode(searchDTO.getDeptCode());
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
