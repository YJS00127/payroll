package payroll.workHistory;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import payroll.common.dto.ResultDTO;
import payroll.entity.AttendanceRecord;
import payroll.entity.WorkHistory;
import payroll.repository.AttendanceRecordRepository;
import payroll.repository.EmployeeRepository;
import payroll.repository.WorkHistoryRepository;
import payroll.workHistory.dto.WorkRequestDTO;
import payroll.workHistory.dto.WorkResponseDTO;
import payroll.workHistory.dto.WorkUpdateRequestDTO;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class WorkServiceImpl implements WorkService{

    private final WorkHistoryRepository workHistoryRepository;
    private final AttendanceRecordRepository attendanceRecordRepository;
    private final EmployeeRepository employeeRepository;

    @Override
    public Page<WorkResponseDTO> findAllByEmpId(WorkRequestDTO requestDTO,
                                                Pageable pageable){
        Long empId = employeeRepository.findIdByCode(requestDTO.getEmpCode())
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 사원입니다."));
        return workHistoryRepository.findAllByEmpId(empId, pageable);
    }


    // 월별 근로 기록 저장
    @Override
    @Transactional
    public void saveWorkHistory(WorkRequestDTO requestDTO){
        Long empId = employeeRepository.findIdByCode(requestDTO.getEmpCode())
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 사원입니다."));
        int year = requestDTO.getYear();
        int month = requestDTO.getMonth();

        if(workHistoryRepository.existsByEmpIdAndYearAndMonth(empId, year, month)){
            throw new EntityExistsException("해당 월의 사원 급여 정보가 이미 존재합니다.");
        }

        WorkHistory workHistory = workRecord(empId, year, month);
        workHistoryRepository.save(workHistory);
    }

    @Override
    @Transactional
    public ResultDTO updateWorkHistory(WorkUpdateRequestDTO updateRequestDTO){
        WorkHistory workHistory = workHistoryRepository.findByEmpIdAndYearAndMonth(updateRequestDTO.getEmpId(), updateRequestDTO.getYear(), updateRequestDTO.getMonth())
                .orElseThrow(() -> new EntityNotFoundException("해당 월의 근로 기록이 없습니다."));
        workHistory.update(
                updateRequestDTO.getWorkDays(),
                updateRequestDTO.getTotalWorkHour(),
                updateRequestDTO.getOvertimeWorkHour(),
                updateRequestDTO.getHolidayWorkHour(),
                updateRequestDTO.getWeeklyHolidayDays()
                );
        return new ResultDTO("success", "근로 기록 수정 성공");
    }

    @Override
    @Transactional
    // 근태 수정 시 월별 근로 기록 자동 수정
    public void autoUpdateWorkHistory(AttendanceRecord attendanceRecord){
        Long empId = attendanceRecord.getEmpId();
        LocalDate workDate = attendanceRecord.getWorkDate();

        LocalDate paymentDate = workDate.getDayOfMonth() >= 15
                ? workDate.plusMonths(1)
                : workDate;

        int year = paymentDate.getYear();
        int month = paymentDate.getMonthValue();

        // 근태 기록 재집계
        WorkHistory calculated = workRecord(empId, year, month);

        // 기존 근로 기록 조회, 없으면 신규 저장
        WorkHistory existing = workHistoryRepository
                .findByEmpIdAndYearAndMonth(empId, year, month)
                .orElseGet(() -> workHistoryRepository.save(calculated));

        existing.update(
                calculated.getWorkDays(),
                calculated.getTotalWorkHour(),
                calculated.getOvertimeWorkHour(),
                calculated.getHolidayWorkHour(),
                calculated.getWeeklyHolidayDays()
        );
    }

//    @Override
//    @Transactional
//    public ResultDTO deleteWorkHistory(Long id){
//        WorkHistory workHistory = workHistoryRepository.findById(id)
//                .orElseThrow(() -> new EntityNotFoundException("근로 기록이 없습니다."));
//
//        workHistory.delete();
//
//        return new ResultDTO("success", "근로 기록 삭제 성공");
//    }


    // 한 달(전달 15일 ~ 이번달 14일) 급여 기록
    WorkHistory workRecord(Long empId, int year, int month){
        // 급여지급일
        int paymentDay = 15;
        // year 2026 month 9
        LocalDate start = LocalDate.of(year, month, paymentDay).minusMonths(1);
        LocalDate end = LocalDate.of(year, month, paymentDay).minusDays(1);

        List<AttendanceRecord> records =
                attendanceRecordRepository.findByEmpIdPeriod(empId, start, end);

        // 근로일수
        int workDays = (int) records.stream()
                .filter(record ->
                        !record.getStatus().equals("결근"))
                .count();

        // 총 근로시간 (휴일 제외)
        int totalWorkHour = records.stream()
                .filter(record -> {
                    LocalDate recordDate = record.getWorkDate();
                    return recordDate.getDayOfWeek() != DayOfWeek.SUNDAY;
                })
                .mapToInt(AttendanceRecord::getWorkHour)
                .sum();

        // 연장 근로시간
        int overtimeWorkHour = records.stream()
                .mapToInt(AttendanceRecord::getOvertimeWorkHour)
                .sum();

        // 휴일 근로시간
        int holidayWorkHour = records.stream()
                .filter(record -> {
                    LocalDate recordDate = record.getWorkDate();
                    return recordDate.getDayOfWeek() == DayOfWeek.SUNDAY;
                })
                .mapToInt(AttendanceRecord::getHolidayWorkHour)
                .sum();

        int weeklyHolidayDays = calculateWeeklyHolidays(empId, start, end);

        return WorkHistory.builder()
                .empId(empId)
                .year(year)
                .month(month)
                .workDays(workDays)
                .totalWorkHour(totalWorkHour)
                .overtimeWorkHour(overtimeWorkHour)
                .holidayWorkHour(holidayWorkHour)
                .weeklyHolidayDays(weeklyHolidayDays)
                .build();
    }

    // 주휴일 수
    private int calculateWeeklyHolidays(Long empId, LocalDate start, LocalDate end){
        return (int) start.datesUntil(end.plusDays(1))
                // 특정 월에서 일요일만 필터링
                .filter(date -> date.getDayOfWeek() == DayOfWeek.SUNDAY)
                // 발생된 주차일 수
                .filter(sunday -> isExistWeeklyHoliday(empId, sunday.minusDays(6)))
                .count();
    }


    // 주휴일 유무 확인
    private boolean isExistWeeklyHoliday(Long empId, LocalDate monday){

        return IntStream.range(0,5)
                .mapToObj(monday::plusDays)
                .allMatch(date -> attendanceRecordRepository
                        .findByEmpIdAndWorkDate(
                                empId,
                                date
                        )
                        .map(record -> !record.getStatus().equals("결근"))
                .orElse(true));
    }
}
