package payroll.department.dto;


import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DeptCreateResponseDTO {
    private String message;
    private String result;
}
