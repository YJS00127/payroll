package payroll.attendance;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import payroll.attendance.dto.AttendanceRequestDTO;
import payroll.attendance.dto.AttendanceResponseDTO;
import payroll.attendance.dto.AttendanceResultDTO;
import payroll.attendance.dto.AttendanceSearchDTO;
import payroll.entity.AttendanceRecord;
import payroll.entity.Employee;
import payroll.repository.AttendanceRecordRepository;
import payroll.repository.EmployeeRepository;
import payroll.workHistory.WorkService;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AttendanceServiceImpl implements AttendanceService{

    private final AttendanceRecordRepository attendanceRecordRepository;
    private final EmployeeRepository employeeRepository;
    private final WorkService workService;

    @Override
    public Page<AttendanceResponseDTO> findAttendance(AttendanceSearchDTO searchDTO,
                                                     Pageable pageable){
        Specification<AttendanceRecord> spec = createSpec(searchDTO);

        return attendanceRecordRepository.findAll(spec, pageable)
                .map(attendanceRecord -> {
                    Employee emp = employeeRepository.findById(attendanceRecord.getEmpId())
                            .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 사원입니다."));

                    String empCode = emp.getCode();
                    String empName = emp.getName();

                    return new AttendanceResponseDTO(attendanceRecord, empCode, empName);
                });
    }

    @Override
    @Transactional
    public AttendanceResultDTO saveAttendance(AttendanceRequestDTO requestDTO){
        Long empId = employeeRepository.findIdByCode(requestDTO.getEmpCode());

        AttendanceRecord attendance = requestDTO.toEntity(empId);

        if(attendanceRecordRepository.existsByEmpIdAndWorkDate(empId, attendance.getWorkDate())){
            throw new EntityExistsException("해당 날짜의 근로 기록이 이미 존재합니다.");
        }

        attendanceRecordRepository.save(attendance);

        return new AttendanceResultDTO("success", "근로 정보 저장 성공");
    }

    @Override
    @Transactional
    public AttendanceResultDTO updateAttendance(AttendanceRequestDTO requestDTO){
        Long empId = employeeRepository.findIdByCode(requestDTO.getEmpCode());
        AttendanceRecord attendance = attendanceRecordRepository.findById(empId)
                .orElseThrow(() -> new EntityNotFoundException("해당 근로 정보가 존재하지 않습니다."));

        attendance.update(requestDTO, empId);

        workService.autoUpdateWorkHistory(attendance);

        return new AttendanceResultDTO("success", "근로 정보 수정 성공");
    }

    @Override
    @Transactional
    public AttendanceResultDTO deleteAttendance(Long id){
        AttendanceRecord attendance = attendanceRecordRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("근로 정보가 존재하지 않습니다."));

        attendance.delete();

        return new AttendanceResultDTO("success", "근로 정보 삭제 성공");
    }


    private Specification<AttendanceRecord> createSpec(AttendanceSearchDTO searchDTO){
        return((root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            Long empId = employeeRepository.findIdByCode(searchDTO.getEmpCode());

            predicates.add(cb.equal(root.get("deleteYn"), "N"));
            predicates.add(cb.equal(root.get("empId"), empId));
            if(searchDTO.getYear() != null){
                predicates.add(cb.equal(root.get("year"), searchDTO.getYear()));
            }
            if(searchDTO.getMonth() != null){
                predicates.add(cb.equal(root.get("month"), searchDTO.getMonth()));
            }
            if(searchDTO.getDay() != null){
                predicates.add(cb.equal(root.get("day"), searchDTO.getDay()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        }
        );
    }
}
