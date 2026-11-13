package payroll.view;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewController {
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
}
