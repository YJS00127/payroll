package payroll.attendance;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.Predicate;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import payroll.attendance.dto.AttendanceRequestDTO;
import payroll.attendance.dto.AttendanceResponseDTO;
import payroll.attendance.dto.AttendanceSearchDTO;
import payroll.common.dto.ResultDTO;
import payroll.common.excel.ExcelRow;
import payroll.common.excel.ExcelService;
import payroll.common.excel.ExcelValidator;
import payroll.entity.AttendanceRecord;
import payroll.entity.Employee;
import payroll.holiday.HolidayService;
import payroll.repository.AttendanceRecordRepository;
import payroll.repository.EmployeeRepository;
import payroll.workHistory.WorkService;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AttendanceServiceImpl implements AttendanceService {

    private final AttendanceRecordRepository attendanceRecordRepository;
    private final EmployeeRepository employeeRepository;
    private final WorkService workService;
    private final HolidayService holidayService;
    private final ExcelService excelService;
    private final ExcelValidator excelValidator;

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
    public void downloadAttendanceExcel(HttpServletResponse response) throws IOException {
        List<String> headers = List.of(
                "사원코드", "근로일", "출근 시간", "퇴근 시간", "근로 상태");

        excelService.downloadExcel("근태등록", headers, response);
    }

    @Override
    @Transactional
    public ResultDTO saveAttendance(AttendanceRequestDTO requestDTO) {
        if(!StringUtils.hasText(requestDTO.getEmpCode())) {
            throw new IllegalArgumentException("사원을 선택해주세요");
        }

        Employee emp = employeeRepository.findByCode(requestDTO.getEmpCode())
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 사원입니다"));

        if(requestDTO.getStatus().equals("휴가")) {
            throw new IllegalArgumentException("해당 날짜에 휴가가 존재합니다");
        }

        if(attendanceRecordRepository.existsByEmpIdAndWorkDate(emp.getId(), requestDTO.getWorkDate())) {
            throw new EntityExistsException("이미 존재하는 근로 기록입니다");
        }

        Map<String, Integer> work = calculateWorkHour(requestDTO, emp);

        AttendanceRecord attendance;

        if(holidayService.isHoliday(requestDTO.getWorkDate())){
            attendance = AttendanceRecord.builder()
                    .empId(emp.getId())
                    .workDate(requestDTO.getWorkDate())
                    .checkIn(requestDTO.getCheckIn())
                    .checkOut(requestDTO.getCheckOut())
                    .workMinutes(work.get("workMinutes"))
                    .overtimeWorkMinutes(work.get("overtimeWorkMinutes"))
                    .holidayWorkMinutes(work.get("holidayWorkMinutes"))
                    .deductionMinutes(work.get("deductionMinutes"))
                    .status("휴일 근로")
                    .build();
        }else {
            attendance = AttendanceRecord.builder()
                    .empId(emp.getId())
                    .workDate(requestDTO.getWorkDate())
                    .checkIn(requestDTO.getCheckIn())
                    .checkOut(requestDTO.getCheckOut())
                    .workMinutes(work.get("workMinutes"))
                    .overtimeWorkMinutes(work.get("overtimeWorkMinutes"))
                    .holidayWorkMinutes(work.get("holidayWorkMinutes"))
                    .status(requestDTO.getStatus())
                    .build();
        }

        attendanceRecordRepository.save(attendance);
        workService.autoSaveWork(attendance);

        return new ResultDTO("success", "근로 정보 저장 성공");
    }

    @Override
    @Transactional
    public ResultDTO uploadAttendanceExcel(MultipartFile file) throws IOException{
        List<String> headers = List.of(
                "사원코드", "근로일", "출근 시간", "퇴근 시간", "근로 상태");

        List<ExcelRow> rows = excelService.readExcel(file, headers);

        if(rows.isEmpty()){
            throw new IllegalArgumentException("등록할 근태 정보가 없습니다");
        }

        for(ExcelRow row : rows){
            int rowNum = row.getRowNum();

            try{
                List<String> v = row.getValues();

                AttendanceRequestDTO dto = new AttendanceRequestDTO();

                dto.setEmpCode(v.get(0));
                dto.setWorkDate(LocalDate.parse(v.get(1)));
                dto.setCheckIn(LocalTime.parse(v.get(2)));
                dto.setCheckOut(LocalTime.parse(v.get(3)));
                dto.setStatus(v.get(4));

                excelValidator.validateDTO(dto);
                saveAttendance(dto);
            }
            catch(IllegalArgumentException e){
                throw new IllegalArgumentException(rowNum + "행: " + e.getMessage());
            }
        }
        return new ResultDTO("success", "근태 " + rows.size() + "개 등록 성공");
    }


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

        attendance.update(requestDTO, empId,
                work.get("workMinutes"),
                work.get("overtimeWorkMinutes"),
                work.get("holidayWorkMinutes"),
                work.get("deductionMinutes")
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
                Long empId = employeeRepository.findIdByNameOrCode(keyword)
                        .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 사원입니다"));
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
        int deductionMinutes = 0;

        if(requestDTO.getStatus().equals("조퇴") || requestDTO.getStatus().equals("지각")){
            deductionMinutes = (int) ChronoUnit.MINUTES.between(requestDTO.getCheckIn(), requestDTO.getCheckOut());
        }
        if(requestDTO.getStatus().equals("결근")){
            deductionMinutes = emp.getContractDailyWorkMinutes();
        }

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
                "holidayWorkMinutes", holidayWorkMinutes,
                "deductionMinutes", deductionMinutes
        );
    }
}
