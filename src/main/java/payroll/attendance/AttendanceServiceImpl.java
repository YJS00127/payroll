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
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import payroll.attendance.dto.AttendanceRequestDTO;
import payroll.attendance.dto.AttendanceResponseDTO;
import payroll.attendance.dto.AttendanceSearchDTO;
import payroll.common.dto.ResultDTO;
import payroll.entity.AttendanceRecord;
import payroll.entity.Employee;
import payroll.repository.AttendanceRecordRepository;
import payroll.repository.EmployeeRepository;
import payroll.workHistory.WorkService;

import java.time.DayOfWeek;
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
    public Page<AttendanceResponseDTO> getAttendance(AttendanceSearchDTO searchDTO,
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
    public AttendanceResponseDTO getAttendanceById(Long id){
        AttendanceRecord attendanceRecord = attendanceRecordRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 근태 기록입니다."));

        Employee emp = employeeRepository.findById(attendanceRecord.getEmpId())
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 사원입니다."));

        return new AttendanceResponseDTO(attendanceRecord, emp.getCode(), emp.getName());
    }

    @Override
    @Transactional
    public ResultDTO saveAttendance(AttendanceRequestDTO requestDTO){
        Employee emp = employeeRepository.findByCode(requestDTO.getEmpCode())
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 사원입니다."));

        if(attendanceRecordRepository.existsByEmpIdAndWorkDate(emp.getId(), requestDTO.getWorkDate())){
            throw new EntityExistsException("해당 날짜의 근로 기록이 이미 존재합니다.");
        }

        int holidayWorkHour = 0;
        int workHour = 0;
        int overtimeWorkHour = 0;

        long totalHour = ChronoUnit.HOURS.between(requestDTO.getCheckIn(), requestDTO.getCheckOut());
        int dailyWorkHour = emp.getContractDailyWorkHour();

        if(requestDTO.getWorkDate().getDayOfWeek() == DayOfWeek.SUNDAY){
            holidayWorkHour = (int) ChronoUnit.HOURS.between(requestDTO.getCheckIn(), requestDTO.getCheckOut());
        } else{
            workHour = (int)totalHour - 1;
            overtimeWorkHour = (int) Math.max(totalHour-dailyWorkHour, 0);
        }

        AttendanceRecord attendance = requestDTO.toEntity(emp.getId(), workHour, overtimeWorkHour, holidayWorkHour);

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

        Map<String, Employee> empMap = employeeRepository.findByCodeIn(empCodes).stream()
                .collect(Collectors.toMap(
                        Employee::getCode,
                        emp -> emp
                ));

        if(empMap.size() != empCodes.size()){
            throw new EntityNotFoundException("존재하지 않는 사원이 포함되어 있습니다.");
        }

        record AttendanceKey(Long empId, LocalDate workDate) {}

        Set<AttendanceKey> keys = requestDTOs.stream()
                .map(dto -> new AttendanceKey(
                        empMap.get(dto.getEmpCode()).getId(),
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
                .map(dto -> {
                    Employee emp = empMap.get(dto.getEmpCode());

                    int workHour = 0;
                    int overtimeWorkHour = 0;
                    int holidayWorkHour = 0;

                    long totalHour = ChronoUnit.HOURS.between(dto.getCheckIn(), dto.getCheckOut());
                    int dailyWorkHour = emp.getContractDailyWorkHour();

                    if (totalHour <= 0) {
                        throw new IllegalArgumentException("출퇴근 시간을 확인해주세요.");
                    }

                    if(dto.getWorkDate().getDayOfWeek() == DayOfWeek.SUNDAY){
                        holidayWorkHour = (int) totalHour;
                    } else{
                        workHour = (int) totalHour;
                        overtimeWorkHour = (int) Math.max(totalHour- dailyWorkHour, 0);
                    }
                    return dto.toEntity(emp.getId(), workHour, overtimeWorkHour, holidayWorkHour);

                })
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

            if(StringUtils.hasText(searchDTO.getKeyword())){

                String keyword = searchDTO.getKeyword().trim() + "%";

                predicates.add(cb.or(
                        cb.like(root.get("name"), keyword),
                        cb.like(root.get("code"), keyword)
                ));
            }
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
