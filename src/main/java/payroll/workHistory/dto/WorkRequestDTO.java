package payroll.workHistory.dto;

import lombok.Getter;
import payroll.entity.WorkHistory;

@Getter
public class WorkRequestDTO {
    private String empCode;
    private int year;
    private int month;

    public WorkHistory toEntity(Long empId){
        return WorkHistory.builder()
                .empId(empId)
                .year(this.year)
                .month(this.month)
                .build();
    }
}
