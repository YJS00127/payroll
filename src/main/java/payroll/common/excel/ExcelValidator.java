package payroll.common.excel;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class ExcelValidator {
    private final Validator validator;

    public<T> void validateDTO(T dto){
        Set<ConstraintViolation<T>> violations = validator.validate(dto);

        if(!violations.isEmpty()){
            throw new IllegalArgumentException(violations.iterator().next().getMessage());
        }
    }
}
