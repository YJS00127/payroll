package payroll.workHistory;

import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import payroll.entity.AttendanceRecord;
import payroll.entity.Employee;
import payroll.entity.WorkHistory;
import payroll.holiday.HolidayService;
import payroll.repository.AttendanceRecordRepository;
import payroll.repository.EmployeeRepository;
import payroll.repository.WorkHistoryRepository;
import payroll.workHistory.dto.WorkResponseDTO;
import payroll.workHistory.dto.WorkSearchDTO;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class WorkServiceImpl implements WorkService {

    private final WorkHistoryRepository workHistoryRepository;
    private final AttendanceRecordRepository attendanceRecordRepository;
    private final EmployeeRepository employeeRepository;
    private final HolidayService holidayService;

    @Override
    public List<WorkResponseDTO> findWork(WorkSearchDTO searchDTO) {
        Specification<WorkHistory> spec = createSpec(searchDTO);

        return workHistoryRepository.findAll(spec, Sort.by("year", "month"))
                .stream()
                .map(workHistory -> {
                    Employee emp = employeeRepository.findById(workHistory.getEmpId())
                            .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 사원입니다"));

                    return new WorkResponseDTO(workHistory, emp);
                }).toList();
    }

    @Override
    @Transactional
    public void autoSaveWork(AttendanceRecord attendance) {
        Long empId = attendance.getEmpId();
        int year = attendance.getWorkDate().getYear();
        int month = attendance.getWorkDate().getMonthValue();

        WorkHistory workHistory = workHistoryRepository.findByEmpIdAndYearAndMonth(empId, year, month)
                .orElseGet(() -> workHistoryRepository.save(
                        WorkHistory.builder()
                                .empId(empId)
                                .year(year)
                                .month(month)
                                .build()
                ));

        workHistory.add(attendance);

        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());

        List<AttendanceRecord> records = attendanceRecordRepository.findByEmpIdPeriod(empId, start.minusDays(6), end);

        workHistory.updateWeeklyHolidayDays(
                calculateWeeklyHolidays(records, start, end)
        );
    }

    @Override
    @Transactional
    // 근태 수정 시 월별 근로 기록 자동 수정
    public void autoUpdateWorkHistory(Long empId, int year, int month) {
        // 근태 기록 재집계
        WorkHistory calculated = workRecord(empId, year, month);

        // 기존 근로 기록 조회, 없으면 신규 저장
        WorkHistory existing = workHistoryRepository
                .findByEmpIdAndYearAndMonth(empId, year, month)
                .orElseGet(() -> workHistoryRepository.save(calculated));

        existing.update(
                calculated.getWorkDays(),
                calculated.getTotalWorkMinutes(),
                calculated.getOvertimeWorkMinutes(),
                calculated.getHolidayWorkMinutes(),
                calculated.getWeeklyHolidayDays(),
                calculated.getDeductionMinutes()
        );
    }

    private Specification<WorkHistory> createSpec(WorkSearchDTO searchDTO) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.hasText(searchDTO.getKeyword())) {
                String keyword = searchDTO.getKeyword().trim();
                Long empId = employeeRepository.findIdByNameOrCode(keyword)
                        .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 사원입니다"));

                predicates.add(cb.equal(root.get("empId"), empId));
            }

            if (searchDTO.getYear() != null) {
                predicates.add(cb.equal(root.get("year"), searchDTO.getYear()));
            }
            if (searchDTO.getMonth() != null) {
                predicates.add(cb.equal(root.get("month"), searchDTO.getMonth()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }


    // 한달 근무 기록
    WorkHistory workRecord(Long empId, int year, int month) {
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());

        List<AttendanceRecord> records =
                attendanceRecordRepository.findByEmpIdPeriod(empId, start.minusDays(6), end);

        List<AttendanceRecord> monthlyRecords = records.stream()
                .filter(record -> !record.getWorkDate().isBefore(start))
                .toList();

        // 근로일수
        int workDays = (int) monthlyRecords.stream()
                .filter(record ->
                        !record.getStatus().equals("결근"))
                .count();

        // 한달 간 총 근로시간
        int totalWorkMinutes = monthlyRecords.stream()
                .mapToInt(AttendanceRecord::getWorkMinutes)
                .sum();

        // 한달 간 총 연장 근로시간
        int overtimeWorkMinutes = monthlyRecords.stream()
                .mapToInt(AttendanceRecord::getOvertimeWorkMinutes)
                .sum();

        // 한달 간 총 휴일 근로시간
        int holidayWorkMinutes = monthlyRecords.stream()
                .mapToInt(AttendanceRecord::getHolidayWorkMinutes)
                .sum();

        // 한달 간 총 공제시간
        int deductionMinutes = monthlyRecords.stream()
                .mapToInt(AttendanceRecord::getDeductionMinutes)
                .sum();

        int weeklyHolidayDays = calculateWeeklyHolidays(records, start, end);

        return WorkHistory.builder()
                .empId(empId)
                .year(year)
                .month(month)
                .workDays(workDays)
                .totalWorkMinutes(totalWorkMinutes)
                .overtimeWorkMinutes(overtimeWorkMinutes)
                .holidayWorkMinutes(holidayWorkMinutes)
                .weeklyHolidayDays(weeklyHolidayDays)
                .deductionMinutes(deductionMinutes)
                .build();
    }

    // 주휴일 수
    private int calculateWeeklyHolidays(List<AttendanceRecord> records, LocalDate start, LocalDate end) {
        Map<LocalDate, AttendanceRecord> attendanceMap = records.stream()
                .collect(Collectors.toMap(
                        AttendanceRecord::getWorkDate,
                        record -> record
                ));

        log.info("집계 시작일: {}, 집계 종료일: {}", start, end);

        return (int) start.datesUntil(end.plusDays(1))
                .filter(date -> date.getDayOfWeek() == DayOfWeek.SUNDAY)
                .filter(sunday -> !sunday.isAfter(LocalDate.now()))
                .filter(sunday -> {
                        boolean result = isExistWeeklyHoliday(attendanceMap, sunday.minusDays(6));

                        log.info("주휴일: {}, 발생여부: {}", sunday, result);

                        return result;
                }).count();
    }

    // 주휴일 유무 확인
    private boolean isExistWeeklyHoliday(Map<LocalDate, AttendanceRecord> attendanceMap, LocalDate monday) {
        return IntStream.range(0, 5)
                .mapToObj(monday::plusDays)
                .allMatch(date -> {
                    if (holidayService.isHoliday(date)) {
                        log.info("날짜: {}, 공휴일", date);
                        return true;
                    }
                    AttendanceRecord record = attendanceMap.get(date);

                    log.info("날짜: {}, 근태상태: {}",
                            date,
                            record == null ? "기록 없음" : record.getStatus());

                    return record != null && !record.getStatus().equals("결근");
                });
    }
}
