package payroll.view;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewController {
    @GetMapping
    public String toEmp() {
        return "emp";
    }

    @GetMapping("/emp")
    public String emp() {
        return "emp";
    }

    @GetMapping("/dept")
    public String dept() {
        return "dept";
    }

    @GetMapping("/attendance")
    public String attendance(){
        return "attendance";
    }

    @GetMapping("/work")
    public String work(){
        return "work";
    }

    @GetMapping("/insurance")
    public String insurance(){
        return "insurance";
    }

    @GetMapping("/salary")
    public String salary() {
        return "salary";
    }

    @GetMapping("/leave")
    public String leave(){
        return "leave";
    }

    @GetMapping("/payroll")
    public String payroll(){
        return "payroll";
    }

    @GetMapping("/payslip")
    public String payslip() {
        return "payslip";
    }
}
