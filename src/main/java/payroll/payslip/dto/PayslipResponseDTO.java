package payroll.payslip.dto;

import lombok.Getter;
import payroll.entity.Employee;
import payroll.entity.PayrollHistory;

import java.time.LocalDate;

@Getter
public class PayslipResponseDTO {
    private int year;
    private int month;
    private String empName;
    private String empCode;
    private LocalDate confirmedAt;
    private LocalDate paymentDate;

    public PayslipResponseDTO(PayrollHistory payrollHistory, Employee emp){
        this.year = payrollHistory.getYear();
        this.month = payrollHistory.getMonth();
        this.empName = emp.getName();
        this.empCode = emp.getCode();
        this.confirmedAt = payrollHistory.getConfirmedAt();
        this.paymentDate = payrollHistory.getPaymentDate();
    }
}
