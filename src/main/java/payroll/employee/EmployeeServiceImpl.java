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
import org.springframework.web.bind.annotation.RequestBody;
import payroll.common.dto.ResultDTO;
import payroll.common.excel.ExcelService;
import payroll.employee.dto.*;
import payroll.entity.*;
import payroll.repository.*;
import payroll.salaryHistory.SalaryHistoryService;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService{

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final LeaveRecordRepository leaveRecordRepository;
    private final ExcelService excelService;

    @Override
    public Page<EmpResponseDTO> findAllEmp(EmpSearchDTO searchDTO, Pageable pageable){
        Specification<Employee> spec = createSpec(searchDTO);

        return employeeRepository.findAll(spec, pageable)
                .map(employee -> {
                    String deptName = departmentRepository.findNameById(employee.getDeptId());
                    return new EmpResponseDTO(employee, deptName);
                });
    }

    @Override
    public EmpResponseDTO findEmpById(Long id) {
        Employee emp =  employeeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 사원입니다"));

        String deptName = departmentRepository.findNameById(emp.getDeptId());

        return new EmpResponseDTO(emp, deptName);
    }

    @Override
    public byte[] downloadEmpExcel() throws IOException{
        List<String> headers = List.of(
                "이름", "사원코드", "부서명", "직급", "입사일", "생년월일",
                "성별", "시급", "월평균 급여", "소정근로시간");

        return excelService.downloadExcel("사원등록", headers);
    }

    @Override
    @Transactional
    public ResultDTO createEmp(EmpRequestDTO requestDTO) {
        Long deptId = departmentRepository.findIdByName(requestDTO.getDeptName())
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 부서입니다"));
        Employee emp = requestDTO.toEntity(deptId);

        if(employeeRepository.existsByCode(emp.getCode())){
            throw new EntityExistsException("해당 코드의 사원이 이미 존재합니다");
        }

        employeeRepository.save(emp);

        return new ResultDTO("success", "사원 저장 성공");
    }

    @Override
    @Transactional
    public ResultDTO createEmpAll(List<EmpRequestDTO> requestDTOs){
        if(requestDTOs == null || requestDTOs.isEmpty()){
            throw new IllegalArgumentException("등록할 사원이 입력되지 않았습니다");
        }

        Set<String> codes = requestDTOs.stream()
                .map(EmpRequestDTO::getCode)
                .collect(Collectors.toSet());

        if(codes.size() != requestDTOs.size()){
            throw new EntityExistsException("중복되는 사원코드가 존재합니다");
        }

        if(employeeRepository.existsByCodeIn(codes)){
            throw new EntityExistsException("이미 등록된 사원코드가 존재합니다");
        }

        Set<String> deptNames = requestDTOs.stream()
                .map(EmpRequestDTO::getDeptName)
                .collect(Collectors.toSet());

        Map<String, Long> deptMap = departmentRepository.findByNameInAndDeleteYn(deptNames, "N").stream()
                .collect(Collectors.toMap(
                        Department::getName,
                        Department::getId
                ));

        if(deptNames.size() != deptMap.size()){
            throw new EntityNotFoundException("존재하지 않는 부서가 포함되어있습니다.");
        }

        List<Employee> employees = requestDTOs.stream()
                .map(dto -> dto.toEntity(deptMap.get(dto.getDeptName())))
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

        return new ResultDTO("success", "사원 등록 성공");
    }

    @Override
    @Transactional
    public ResultDTO updateEmp(Long id, EmpUpdateRequestDTO requestDTO){
        Long deptId = departmentRepository.findIdByName(requestDTO.getDeptName())
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 부서입니다"));

        Employee emp = employeeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 사원입니다"));

        emp.update(requestDTO, deptId);

        return new ResultDTO("success", "사원 수정 성공");
    }

    @Override
    @Transactional
    public void updateHourlyWage(Long id, int hourlyWage){
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 사원입니다"));

        employee.updateHourlyWage(hourlyWage);
    }

    @Override
    @Transactional
    public ResultDTO deleteEmp(Long id){
        Employee emp = employeeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 사원입니다"));

        emp.delete();

        return new ResultDTO("success", "사원 삭제 성공");
    }

    @Override
    @Transactional
    public ResultDTO deleteEmps(List<Long> id){
        List<Employee> employees = employeeRepository.findAllById(id);

        if(employees.size() != Set.copyOf(id).size()
        || employees.stream().allMatch(emp -> emp.getDeleteYn().equals("Y"))){
            throw new IllegalArgumentException("존재하지 않는 사원이 포함되어있습니다");
        }

        employees.forEach(Employee::delete);

        return new ResultDTO("success", "사원 삭제 성공");
    }

    private Specification<Employee> createSpec(EmpSearchDTO searchDTO){
        return(root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.equal(root.get("deleteYn"), "N"));

            if(StringUtils.hasText(searchDTO.getDeptName())){
                Long deptId = departmentRepository.findIdByName(searchDTO.getDeptName())
                        .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 부서입니다"));
                predicates.add(cb.equal(root.get("deptId"), deptId));
            }

            if(StringUtils.hasText(searchDTO.getKeyword())){

                String keyword = searchDTO.getKeyword().trim();

                predicates.add(cb.or(
                        cb.like(root.get("name"), keyword),
                        cb.like(root.get("code"), keyword)
                ));
            }

            if(StringUtils.hasText(searchDTO.getRank())){
                predicates.add(cb.like(
                        root.get("rank"),
                        "%" + searchDTO.getRank() + "%"
                ));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
