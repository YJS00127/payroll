package payroll.payroll.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class PayrollHistoryPaymentDTO {
    private int basicSalary;
    private int weeklyHolidayPay;
    private int retroactivePay;
    private int deductionAmount;
    private int monthlyLeavePay;
    private int mealAllowance;
    private int overTimePay;
    private int unpaidAllowance;
    private String unpaidAllowanceReason;
    private int otherAllowance;
    private int holidayWorkPay;

    public int grossSalary(){
        return this.basicSalary +
                this.weeklyHolidayPay +
                this.retroactivePay +
                this.deductionAmount +
                this.monthlyLeavePay +
                this.mealAllowance +
                this.overTimePay +
                this.unpaidAllowance +
                this.otherAllowance +
                this.holidayWorkPay;
    }
}
