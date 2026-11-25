package payroll.leaveUsage;

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
import payroll.entity.AttendanceRecord;
import payroll.entity.Employee;
import payroll.entity.LeaveRecord;
import payroll.entity.LeaveUsage;
import payroll.leaveUsage.dto.LeaveUsageRequestDTO;
import payroll.leaveUsage.dto.LeaveUsageResponseDTO;
import payroll.leaveUsage.dto.LeaveUsageSearchDTO;
import payroll.repository.AttendanceRecordRepository;
import payroll.repository.EmployeeRepository;
import payroll.repository.LeaveRecordRepository;
import payroll.repository.LeaveUsageRepository;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LeaveUsageServiceImpl implements LeaveUsageService{
    private final LeaveUsageRepository leaveUsageRepository;
    private final EmployeeRepository employeeRepository;
    private final AttendanceRecordRepository attendanceRecordRepository;
    private final LeaveRecordRepository leaveRecordRepository;

    @Override
    public Page<LeaveUsageResponseDTO> findLeaveUsage(LeaveUsageSearchDTO searchDTO, Pageable pageable){
        Specification<LeaveUsage> spec = createSpec(searchDTO);
        return leaveUsageRepository.findAll(spec, pageable)
                .map(leaveUsage -> {
                    Employee emp = employeeRepository.findById(leaveUsage.getEmpId())
                            .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 사원입니다")) ;
                    String empCode = emp.getCode();
                    String empName = emp.getName();

                    return new LeaveUsageResponseDTO(leaveUsage, empCode, empName, leaveUsage.getLeaveType());
                });
    }

    @Override
    public LeaveUsageResponseDTO findLeaveUsageById(Long id){
        LeaveUsage leaveUsage = leaveUsageRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 휴가 이력입니다"));

        Employee emp = employeeRepository.findById(leaveUsage.getEmpId())
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 사원입니다"));

        return new LeaveUsageResponseDTO(leaveUsage, emp.getCode(), emp.getName(), leaveUsage.getLeaveType());
    }

    @Override
    @Transactional
    public ResultDTO saveLeaveUsage(LeaveUsageRequestDTO requestDTO){
        Long empId = employeeRepository.findIdByCode(requestDTO.getEmpCode())
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 사원입니다"));
        LocalDate startDate = requestDTO.getStartDate();
        LocalDate endDate = requestDTO.getEndDate();

        System.out.println(requestDTO.getLeaveType());

        double usedDays = 0;
        if(requestDTO.getLeaveType().equals("반차")){
            usedDays = 0.5;
        }
        if(requestDTO.getLeaveType().equals("반반차")){
            usedDays = 0.25;
        }
        if(requestDTO.getLeaveType().equals("휴가")){
            usedDays = calculateUsedDays(startDate, endDate);
        }

        System.out.println(usedDays);

        LeaveRecord leaveRecord = leaveRecordRepository.findByEmpIdAndYearAndMonth(empId, startDate.getYear(), startDate.getMonthValue())
                .orElseThrow(() -> new EntityNotFoundException("휴가 정보가 존재하지 않습니다"));

        Long id = leaveRecord.getId();
        double remainingLeave = leaveRecord.getRemainingLeave();

        if(remainingLeave < usedDays){
            throw new IllegalArgumentException("잔여 휴가보다 사용 휴가가 많습니다");
        }

        leaveRecord.useLeave(usedDays);

        LeaveUsage leaveUsage = requestDTO.toEntity(empId ,usedDays);

        // 근태 기록이 있는지 확인
        if(attendanceRecordRepository.existsWorkedAttendance(empId, startDate, endDate) == 1){
            throw new EntityExistsException("해당 날에 근무 기록이 존재합니다");
        }
        // 휴가 중복 신청 방지
        if(leaveUsageRepository.existsLeaveUsage(id, empId, startDate, endDate) == 1){
            throw new EntityExistsException("해당 날에 이미 휴가가 존재합니다");
        }

        leaveUsageRepository.save(leaveUsage);

        // 근태 정보 저장
        startDate.datesUntil(endDate)
                .filter(date ->
                        date.getDayOfWeek() != DayOfWeek.SATURDAY && date.getDayOfWeek() != DayOfWeek.SUNDAY)
                .forEach(date -> {
                    AttendanceRecord attRecord = attendanceRecordRepository.findByEmpIdAndWorkDate(empId, date)
                            .orElse(null);

                    if(attRecord == null && requestDTO.getLeaveType().equals("휴가")) {
                            AttendanceRecord attendanceRecord = AttendanceRecord.builder()
                                    .empId(empId)
                                    .workDate(date)
                                    .checkIn(null)
                                    .checkOut(null)
                                    .status("휴가")
                                    .build();
                            attendanceRecordRepository.save(attendanceRecord);
                    }
                });

        return new ResultDTO("success", "휴가 등록 성공");
    }

    @Override
    @Transactional
    public ResultDTO updateLeaveUsage(Long id, LeaveUsageRequestDTO requestDTO){
        LeaveUsage leaveUsage = leaveUsageRepository.findByIdAndDeleteYn(id, "N")
                .orElseThrow(() -> new EntityNotFoundException("휴가 정보가 존재하지 않습니다"));

        // 사원 ID / 신규, 이전 휴가 시작일 / 신규, 이전 휴가 종료일
        Long empId = leaveUsage.getEmpId();

        LocalDate previousStartDate = leaveUsage.getStartDate();
        LocalDate previousEndDate = leaveUsage.getEndDate();
        LocalDate newStartDate = requestDTO.getStartDate();
        LocalDate newEndDate = requestDTO.getEndDate();

        // 이전 휴가 사용일, 신규 휴가 사용일
        double previousUsedDays = calculateUsedDays(previousStartDate, previousEndDate);
        double newUsedDays = calculateUsedDays(newStartDate, newEndDate);

        // 수정 중인 휴가 제외 다른 휴가와 중복 등록 확인
        if(leaveUsageRepository.existsLeaveUsage(id, empId, newStartDate, newEndDate) == 1){
            throw new EntityExistsException("이미 해당 날짜에 휴가가 등록되어있습니다");
        }
        // 휴가 일수 현황 조회
        LeaveRecord leaveRecord = leaveRecordRepository.findByEmpIdAndYearAndMonth(empId, newStartDate.getYear(), newStartDate.getMonthValue())
                .orElseThrow(() -> new EntityNotFoundException("휴가 기록이 존재하지 않습니다"));
        // 잔여 휴가, 사용 휴가일 비교
        if(newUsedDays > leaveRecord.getRemainingLeave() + previousUsedDays){
            throw new IllegalArgumentException("잔여 휴가보다 사용 휴가가 많습니다");
        }
        // 이전 휴가 사용 정보와, 수정된 휴가 사용 정보 다를 시 남은 휴가 재계산
        if(previousUsedDays != newUsedDays){
            leaveRecord.updateLeave(previousUsedDays, newUsedDays);
        }

        // 수정 전 휴가 날짜들, 수정 후 휴가 날짜들
        Set<LocalDate> previousDates = getLeaveDates(previousStartDate, previousEndDate);
        Set<LocalDate> newDates = getLeaveDates(newStartDate, newEndDate);

        // 이전 휴가 날짜에서 중복되는 신규 휴가 날짜 제거 (근태 기록 삭제될 날짜)
        Set<LocalDate> deleteDates = new HashSet<>(previousDates);
        deleteDates.removeAll(newDates);
        // 신규 휴가 날짜에서 중복되는 이전 휴가 날짜 제거 (근태 기록 생성할 날짜)
        Set<LocalDate> saveDates = new HashSet<>(newDates);
        saveDates.removeAll(previousDates);

        // 수정된 휴가 중 근태 존재 확인
        for(LocalDate date : saveDates){
            if(attendanceRecordRepository.existsByEmpIdAndWorkDate(empId, date)){
                throw new IllegalArgumentException("해당날짜에 근태 기록이 존재합니다");
            }
        }

        // 근태 정보 수정
        deleteLeaveAttendance(empId, deleteDates);
        saveLeaveAttendance(empId, saveDates);

        leaveUsage.update(newStartDate, newEndDate, newUsedDays, requestDTO.getLeaveType());

        return new ResultDTO("success", "휴가 수정 완료");
    }

    @Override
    @Transactional
    public ResultDTO deleteLeaveUsage(Long id){
        LeaveUsage leaveUsage = leaveUsageRepository.findByIdAndDeleteYn(id, "N")
                .orElseThrow(() -> new EntityNotFoundException("휴가 정보가 존재하지 않습니다"));

        leaveUsage.delete();

        Long empId = leaveUsage.getEmpId();
        LocalDate startDate = leaveUsage.getStartDate();
        LocalDate endDate = leaveUsage.getEndDate();

        List<AttendanceRecord> attendanceRecords = attendanceRecordRepository.findByEmpIdPeriod(empId, startDate, endDate.plusDays(1));

        attendanceRecords.stream()
                .filter(record -> record.getStatus().equals("휴가"))
                .forEach(attendanceRecordRepository::delete);

        double leaveDays = leaveUsage.getUsedDays();

        LeaveRecord leaveRecord = leaveRecordRepository
                .findByEmpIdAndYearAndMonth(empId, startDate.getYear(), startDate.getMonthValue())
                .orElseThrow(() -> new EntityNotFoundException("해당 직원의 휴가 정보가 없습니다"));

        leaveRecord.cancelLeave(leaveDays);

        return new ResultDTO("success", "휴가 정보 삭제 성공");
    }

    @Override
    @Transactional
    public ResultDTO deleteSelectedLeaveUsage(List<Long> ids){
        List<LeaveUsage> leaveUsages = leaveUsageRepository.findAllById(ids);

        if(leaveUsages.size() != Set.copyOf(ids).size()){
            throw new IllegalArgumentException("존재하지 않는 휴가 이력이 포함되어있습니다");
        }
        leaveUsages.forEach(LeaveUsage::delete);

        return new ResultDTO("success", "휴가 정보 삭제 성공");
    }


    private Specification<LeaveUsage> createSpec(LeaveUsageSearchDTO searchDTO){
        return((root, query, cb) ->{
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.equal(root.get("deleteYn"), "N"));

            if(StringUtils.hasText(searchDTO.getKeyword())) {
                Long empId = employeeRepository.findIdByNameOrCode(searchDTO.getKeyword())
                                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 사원입니다"));
                predicates.add(cb.equal(root.get("empId"), empId));
            }
            if(searchDTO.getYear() != null){
                predicates.add(cb.equal(root.get("year"), searchDTO.getYear()));
            }
            if(searchDTO.getMonth() != null){
                predicates.add(cb.equal(root.get("month"), searchDTO.getMonth()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        });
    }

    // 주말을 제외한 휴가 날짜 반환
    private Set<LocalDate> getLeaveDates(LocalDate startDate, LocalDate endDate) {

        if (startDate == null || endDate == null || startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("휴가 날짜를 다시 입력해주세요");
        }

        // 현재 연도별 휴가 관리 구조에서는 연도에 걸친 신청을 제한
        if (startDate.getYear() != endDate.getYear()) {
            throw new IllegalArgumentException("연도가 다른 휴가는 나누어 신청해주세요");
        }

        // 주말 제외 휴가 날짜 Set으로 반환
        Set<LocalDate> dates = startDate.datesUntil(endDate.plusDays(1))
                .filter(date -> date.getDayOfWeek() != DayOfWeek.SATURDAY
                        && date.getDayOfWeek() != DayOfWeek.SUNDAY)
                .collect(Collectors.toSet());

        if (dates.isEmpty()) {
            throw new IllegalArgumentException("휴가 사용일이 존재하지 않습니다");
        }

        return dates;
    }

    private void saveLeaveAttendance(Long empId, Set<LocalDate> dates){
        dates.forEach(date -> {
            AttendanceRecord attendanceRecord = AttendanceRecord.builder()
                    .empId(empId)
                    .workDate(date)
                    .checkIn(null)
                    .checkOut(null)
                    .status("휴가")
                    .build();

            attendanceRecordRepository.save(attendanceRecord);
        });
    }

    private void deleteLeaveAttendance(Long empId, Set<LocalDate> dates){
        dates.forEach(date -> {
            attendanceRecordRepository.findByEmpIdAndWorkDate(empId, date)
                    .filter(record ->
                            record.getStatus().equals("휴가"))
                    .ifPresent(attendanceRecordRepository::delete);
        });
    }

    private double calculateUsedDays(LocalDate startDate, LocalDate endDate) {
        return (double)startDate.datesUntil(endDate.plusDays(1))
                .filter(date -> date.getDayOfWeek() != DayOfWeek.SATURDAY)
                .filter(date -> date.getDayOfWeek() != DayOfWeek.SUNDAY)
                .count();
    }
}
