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
import payroll.attendance.dto.AttendanceSearchDTO;
import payroll.common.dto.ResultDTO;
import payroll.entity.AttendanceRecord;
import payroll.entity.Employee;
import payroll.repository.AttendanceRecordRepository;
import payroll.repository.EmployeeRepository;
import payroll.workHistory.WorkService;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

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
    public ResultDTO saveAttendance(AttendanceRequestDTO requestDTO){
        Long empId = employeeRepository.findIdByCode(requestDTO.getEmpCode())
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 사원입니다."));

        if(attendanceRecordRepository.existsByEmpIdAndWorkDate(empId, requestDTO.getWorkDate())){
            throw new EntityExistsException("해당 날짜의 근로 기록이 이미 존재합니다.");
        }

        int workHour = (int) ChronoUnit.HOURS.between(requestDTO.getCheckIn(), requestDTO.getCheckOut())-1;

        AttendanceRecord attendance = requestDTO.toEntity(empId);

        attendanceRecordRepository.save(attendance);

        return new ResultDTO("success", "근로 정보 저장 성공");
    }

    @Override
    @Transactional
    public ResultDTO saveAttendanceAll(List<AttendanceRequestDTO> requestDTOs){

        if(requestDTOs == null || requestDTOs.isEmpty()){
            throw new IllegalArgumentException("등록할 근로 정보가 입력되지 않았습니다.");
        }

        Set<String> empCodes = requestDTOs.stream()
                .map(AttendanceRequestDTO::getEmpCode)
                .collect(Collectors.toSet());

        Map<String, Long> empMap = employeeRepository.findByCodeIn(empCodes).stream()
                .collect(Collectors.toMap(
                        Employee::getCode,
                        Employee::getId
                ));

        if(empMap.size() != empCodes.size()){
            throw new EntityNotFoundException("존재하지 않는 사원이 포함되어 있습니다.");
        }

        record AttendanceKey(Long empId, LocalDate workDate) {}

        Set<AttendanceKey> keys = requestDTOs.stream()
                .map(dto -> new AttendanceKey(
                        empMap.get(dto.getEmpCode()),
                        dto.getWorkDate()
                ))
                .collect(Collectors.toSet());

        if(keys.size() != requestDTOs.size()){
            throw new EntityExistsException("중복되는 근로 정보가 존재합니다.");
        }

        Set<Long> empIds = keys.stream()
                .map(AttendanceKey::empId)
                .collect(Collectors.toSet());

        Set<LocalDate> workDates = keys.stream()
                .map(AttendanceKey::workDate)
                .collect(Collectors.toSet());

        List<AttendanceRecord> existing =
                attendanceRecordRepository.findByEmpIdInAndWorkDateIn(empIds, workDates);

        boolean duplicated = existing.stream()
                .anyMatch(record -> keys.contains(
                        new AttendanceKey(
                                record.getEmpId(),
                                record.getWorkDate()
                        )
                ));

        if(duplicated){
            throw new EntityExistsException("이미 등록된 근로 정보가 존재합니다.");
        }

        List<AttendanceRecord> records = requestDTOs.stream()
                .map(dto -> dto.toEntity(empMap.get(dto.getEmpCode())))
                .toList();

        List<AttendanceRecord> saved = attendanceRecordRepository.saveAllAndFlush(records);

        Set<AttendanceKey> updateTargets = new HashSet<>();

        for(AttendanceRecord record : saved) {
            LocalDate date = record.getWorkDate();

            LocalDate paymentDate = date.getDayOfMonth() >= 15
                    ? date.plusMonths(1) : date;

            AttendanceKey target = new AttendanceKey(
                    record.getEmpId(),
                    paymentDate.withDayOfMonth(1)
            );

            if (updateTargets.add(target)) {
                workService.autoUpdateWorkHistory(record);
            }
        }

        return new ResultDTO("success", "근로 정보 다중 등록 성공");
    }


    @Override
    @Transactional
    public ResultDTO updateAttendance(Long id, AttendanceRequestDTO requestDTO){
        Long empId = employeeRepository.findIdByCode(requestDTO.getEmpCode())
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 사원입니다."));

        AttendanceRecord attendance = attendanceRecordRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("해당 근로 정보가 존재하지 않습니다."));

        attendance.update(requestDTO, empId);

        workService.autoUpdateWorkHistory(attendance);

        return new ResultDTO("success", "근로 정보 수정 성공");
    }

    @Override
    @Transactional
    public ResultDTO deleteAttendance(Long id){
        AttendanceRecord attendance = attendanceRecordRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("근로 정보가 존재하지 않습니다."));

        attendanceRecordRepository.delete(attendance);

        return new ResultDTO("success", "근로 정보 삭제 성공");
    }

    @Override
    @Transactional
    public ResultDTO deleteAttendanceAll(List<Long> id){
        List<AttendanceRecord> attendances = attendanceRecordRepository.findAllById(id);

        if(attendances.size() != Set.copyOf(id).size()){
            throw new EntityNotFoundException("존재하지 않은 근로 정보입니다.");
        }

        attendanceRecordRepository.deleteAll(attendances);

        return new ResultDTO("success", "근로 정보 다중 삭제 성공");
    }


    private Specification<AttendanceRecord> createSpec(AttendanceSearchDTO searchDTO){
        return((root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            Long empId = employeeRepository.findIdByCode(searchDTO.getEmpCode())
                    .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 사원입니다."));

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
