package payroll.department;

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
import payroll.department.dto.DeptRequestDTO;
import payroll.department.dto.DeptResponseDTO;
import payroll.department.dto.DeptSearchDTO;
import payroll.department.dto.DeptResultDTO;
import payroll.entity.Department;
import payroll.repository.DepartmentRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DepartmentServiceImpl implements DepartmentService{

    private final DepartmentRepository departmentRepository;

    @Override
    public Page<DeptResponseDTO> findAllDepts(DeptSearchDTO searchDTO, Pageable pageable){
        Specification<Department> spec = createSpec(searchDTO);

        return departmentRepository.findAll(spec, pageable)
                .map(DeptResponseDTO::new);
    }

    @Override
    @Transactional
    public DeptResultDTO saveDept(DeptRequestDTO requestDTO){
        Department department = requestDTO.toEntity();

        if(departmentRepository.existsByCode(department.getCode())){
            throw new EntityExistsException("해당 코드의 부서가 이미 존재합니다.");
        }

        departmentRepository.save(department);

        return new DeptResultDTO("success", "부서 등록 성공");
    }

    @Override
    @Transactional
    public DeptResultDTO updateDept(Long id, DeptRequestDTO requestDTO){
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("부서가 존재하지 않습니다."));
        department.update(requestDTO);

        return new DeptResultDTO("success", "부서 수정 성공");
    }

    @Override
    @Transactional
    public DeptResultDTO deleteDept(Long id){
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("부서가 존재하지 않습니다."));
        department.delete();

        return new DeptResultDTO("success", "부서 삭제 성공");
    }

    private Specification<Department> createSpec(DeptSearchDTO searchDTO){
        return(root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.equal(root.get("deleteYn"), "N"));

            if(searchDTO.getId() != null){
                predicates.add(cb.equal(root.get("id"), searchDTO.getId()));
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

            if(searchDTO.getCreatedDeptDate() != null){
                predicates.add(cb.equal(root.get("createdDeptDate"), searchDTO.getCreatedDeptDate()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
