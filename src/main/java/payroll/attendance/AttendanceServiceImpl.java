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
import payroll.attendance.dto.AttendanceRequestDTO;
import payroll.attendance.dto.AttendanceResponseDTO;
import payroll.attendance.dto.AttendanceSearchDTO;
import payroll.common.dto.ResultDTO;
import payroll.entity.AttendanceRecord;
import payroll.entity.Employee;
import payroll.holiday.HolidayService;
import payroll.repository.AttendanceRecordRepository;
import payroll.repository.EmployeeRepository;
import payroll.workHistory.WorkService;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AttendanceServiceImpl implements AttendanceService {

    private final AttendanceRecordRepository attendanceRecordRepository;
    private final EmployeeRepository employeeRepository;
    private final WorkService workService;
    private final HolidayService holidayService;

    @Override
    public Page<AttendanceResponseDTO> getAttendance(AttendanceSearchDTO searchDTO,
                                                     Pageable pageable) {
        Specification<AttendanceRecord> spec = createSpec(searchDTO);

        return attendanceRecordRepository.findAll(spec, pageable)
                .map(attendanceRecord -> {
                    Employee emp = employeeRepository.findById(attendanceRecord.getEmpId())
                            .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 사원입니다"));

                    return new AttendanceResponseDTO(attendanceRecord, emp);
                });
    }

    @Override
    public AttendanceResponseDTO getAttendanceById(Long id) {
        AttendanceRecord attendanceRecord = attendanceRecordRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 근태 기록입니다"));

        Employee emp = employeeRepository.findById(attendanceRecord.getEmpId())
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 사원입니다"));

        return new AttendanceResponseDTO(attendanceRecord, emp);
    }

    @Override
    @Transactional
    public ResultDTO saveAttendance(AttendanceRequestDTO requestDTO) {
        if((requestDTO.getEmpCode() == null || !StringUtils.hasText(requestDTO.getEmpCode())) &&
                (requestDTO.getEmpName() == null || !StringUtils.hasText(requestDTO.getEmpName()))){
            throw new IllegalArgumentException("사원을 선택해주세요");
        }

        Employee emp = employeeRepository.findByCode(requestDTO.getEmpCode())
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 사원입니다"));

        if(attendanceRecordRepository.existsByEmpIdAndWorkDate(emp.getId(), requestDTO.getWorkDate())) {
            throw new EntityExistsException("해당 날짜의 근로 기록이 이미 존재합니다");
        }

        if(!requestDTO.getStatus().equals("휴가") &&
                requestDTO.getWorkDate().isAfter(LocalDate.now())){
            throw new IllegalArgumentException("근태는 미리 등록할 수 없습니다");
        }

        Map<String, Integer> work = calculateWorkHour(requestDTO, emp);

        AttendanceRecord attendance = AttendanceRecord.builder()
                .empId(emp.getId())
                .workDate(requestDTO.getWorkDate())
                .checkIn(requestDTO.getCheckIn())
                .checkOut(requestDTO.getCheckOut())
                .workMinutes(work.get("workMinutes"))
                .overtimeWorkMinutes(work.get("overtimeWorkMinutes"))
                .holidayWorkMinutes(work.get("holidayWorkMinutes"))
                .status(requestDTO.getStatus())
                .build();

        attendanceRecordRepository.save(attendance);
        workService.autoSaveWork(attendance);

        return new ResultDTO("success", "근로 정보 저장 성공");
    }

//    @Override
//    @Transactional
//    public ResultDTO saveAttendanceAll(List<AttendanceRequestDTO> requestDTOs) {
//        if (requestDTOs == null || requestDTOs.isEmpty()) {
//            throw new IllegalArgumentException("등록할 근로 정보가 입력되지 않았습니다");
//        }
//
//        Set<String> empCodes = requestDTOs.stream()
//                .map(AttendanceRequestDTO::getEmpCode)
//                .collect(Collectors.toSet());
//
//        Map<String, Employee> empMap = employeeRepository.findByCodeIn(empCodes).stream()
//                .collect(Collectors.toMap(
//                        Employee::getCode,
//                        emp -> emp
//                ));
//
//        if (empMap.size() != empCodes.size()) {
//            throw new EntityNotFoundException("존재하지 않는 사원이 포함되어 있습니다");
//        }
//
//        record AttendanceKey(Long empId, LocalDate workDate) {
//        }
//
//        Set<AttendanceKey> keys = requestDTOs.stream()
//                .map(dto -> new AttendanceKey(
//                        empMap.get(dto.getEmpCode()).getId(),
//                        dto.getWorkDate()
//                ))
//                .collect(Collectors.toSet());
//
//        if (keys.size() != requestDTOs.size()) {
//            throw new EntityExistsException("중복되는 근로 정보가 존재합니다");
//        }
//
//        Set<Long> empIds = keys.stream()
//                .map(AttendanceKey::empId)
//                .collect(Collectors.toSet());
//
//        Set<LocalDate> workDates = keys.stream()
//                .map(AttendanceKey::workDate)
//                .collect(Collectors.toSet());
//
//        List<AttendanceRecord> existing =
//                attendanceRecordRepository.findByEmpIdInAndWorkDateIn(empIds, workDates);
//
//        boolean duplicated = existing.stream()
//                .anyMatch(record -> keys.contains(
//                        new AttendanceKey(
//                                record.getEmpId(),
//                                record.getWorkDate()
//                        )
//                ));
//
//        if (duplicated) {
//            throw new EntityExistsException("이미 등록된 근로 정보가 존재합니다");
//        }
//
//        List<AttendanceRecord> records = requestDTOs.stream()
//                .map(dto -> {
//                    Employee emp = empMap.get(dto.getEmpCode());
//
//                    Map<String, Integer> work = calculateWorkHour(dto, emp);
//
//                    return dto.toEntity(
//                            emp.getId(),
//                            work.get("workMinutes"),
//                            work.get("overtimeWorkMinutes"),
//                            work.get("holidayWorkMinutes")
//                    );
//                }).toList();
//
//        List<AttendanceRecord> saved = attendanceRecordRepository.saveAllAndFlush(records);
//
//        Set<AttendanceKey> updateTargets = new HashSet<>();
//
//        for (AttendanceRecord record : saved) {
//            LocalDate date = record.getWorkDate();
//
//            LocalDate paymentDate = date.getDayOfMonth() >= 15
//                    ? date.plusMonths(1) : date;
//
//            AttendanceKey target = new AttendanceKey(
//                    record.getEmpId(),
//                    paymentDate.withDayOfMonth(1)
//            );
//
//            if (updateTargets.add(target)) {
//                workService.autoUpdateWorkHistory(record);
//            }
//        }
//
//        return new ResultDTO("success", "근로 정보 다중 등록 성공");
//    }


    @Override
    @Transactional
    public ResultDTO updateAttendance(Long id, AttendanceRequestDTO requestDTO) {
        Employee emp = employeeRepository.findByCode(requestDTO.getEmpCode())
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 사원입니다"));

        AttendanceRecord attendance = attendanceRecordRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("해당 근로 정보가 존재하지 않습니다"));

        long empId = attendance.getEmpId();
        int year = attendance.getWorkDate().getYear();
        int month = attendance.getWorkDate().getMonthValue();

        Map<String, Integer> work = calculateWorkHour(requestDTO, emp);

        attendance.update(requestDTO, empId, work.get("workMinutes"),
                work.get("overtimeWorkMinutes"),
                work.get("holidayWorkMinutes")
        );

        workService.autoUpdateWorkHistory(empId, year, month);

        return new ResultDTO("success", "근로 정보 수정 성공");
    }

    @Override
    @Transactional
    public ResultDTO deleteAttendance(Long id) {
        AttendanceRecord attendance = attendanceRecordRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("근로 정보가 존재하지 않습니다"));

        attendanceRecordRepository.delete(attendance);
        attendanceRecordRepository.flush();

        long empId = attendance.getEmpId();
        int year = attendance.getWorkDate().getYear();
        int month = attendance.getWorkDate().getMonthValue();

        workService.autoUpdateWorkHistory(empId, year, month);

        return new ResultDTO("success", "근로 정보 삭제 성공");
    }

    @Override
    @Transactional
    public ResultDTO deleteAttendanceAll(List<Long> id) {
        List<AttendanceRecord> attendances = attendanceRecordRepository.findAllById(id);

        if (attendances.size() != Set.copyOf(id).size()) {
            throw new EntityNotFoundException("존재하지 않은 근로 정보입니다");
        }

        attendanceRecordRepository.deleteAll(attendances);
        attendanceRecordRepository.flush();

        Set<String> updateTargets = new HashSet<>();

        for(AttendanceRecord attendance : attendances){
            long empId = attendance.getEmpId();
            int year = attendance.getWorkDate().getYear();
            int month = attendance.getWorkDate().getMonthValue();

            String target = empId + "-" + year + "-" + month;

            if(updateTargets.add(target)){
                workService.autoUpdateWorkHistory(empId, year, month);
            }
        }


        return new ResultDTO("success", "근로 정보 다중 삭제 성공");
    }


    private Specification<AttendanceRecord> createSpec(AttendanceSearchDTO searchDTO) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if(StringUtils.hasText(searchDTO.getKeyword())) {
                String keyword = searchDTO.getKeyword().trim();
                Long empId = employeeRepository.findIdByNameOrCode(keyword);
                predicates.add(cb.or(cb.equal(root.get("empId"), empId)));
            };


            if (searchDTO.getYear() != null) {
                predicates.add(cb.equal(cb.function("YEAR", Integer.class, root.get("workDate")), searchDTO.getYear()));
            }
            if (searchDTO.getMonth() != null) {
                predicates.add(cb.equal(cb.function("MONTH", Integer.class, root.get("workDate")), searchDTO.getMonth()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private Map<String, Integer> calculateWorkHour(AttendanceRequestDTO requestDTO, Employee emp){
        int workMinutes = 0;
        int overtimeWorkMinutes = 0;
        int holidayWorkMinutes = 0;

        LocalDate workDate = requestDTO.getWorkDate();

        int totalWorkMinutes = (int) ChronoUnit.MINUTES.between(requestDTO.getCheckIn(), requestDTO.getCheckOut());

        if(totalWorkMinutes <= 0){
            throw new IllegalArgumentException("출퇴근 시간을 확인해주세요");
        }

        int breakMinutes = 0;

        if(totalWorkMinutes >= 480){
            breakMinutes = 60;
        } else if(totalWorkMinutes >= 240){
            breakMinutes = 30;
        }

        int actualMinutes = totalWorkMinutes - breakMinutes;

        if(workDate.getDayOfWeek() == DayOfWeek.SUNDAY ||
                holidayService.isHoliday(workDate)){
            holidayWorkMinutes = actualMinutes;
        } else{
            workMinutes = Math.min(actualMinutes, emp.getContractDailyWorkMinutes());
            overtimeWorkMinutes = Math.max(actualMinutes-workMinutes, 0);
        }
        return Map.of(
                "workMinutes", workMinutes,
                "overtimeWorkMinutes", overtimeWorkMinutes,
                "holidayWorkMinutes", holidayWorkMinutes
        );
    }
}
