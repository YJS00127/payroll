package payroll.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class EmployeeInDepartmentException extends RuntimeException {
    public EmployeeInDepartmentException(String message) {
        super(message);
    }
}
