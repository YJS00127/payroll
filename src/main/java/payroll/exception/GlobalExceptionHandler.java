package payroll.exception;


import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import payroll.department.dto.DeptCreateResponseDTO;

import java.lang.reflect.Method;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<DeptCreateResponseDTO> handleDataIntegrity(
            DataIntegrityViolationException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(DeptCreateResponseDTO.builder()
                        .message("부서 등록 실패")
                        .result("ERROR")
                        .build());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<DeptCreateResponseDTO> handlerValidation(
            MethodArgumentNotValidException e) {

        String message = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getDefaultMessage())
                .collect(Collectors.joining("\n"));

        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(DeptCreateResponseDTO.builder()
                        .message(message)
                        .result("ERROR")
                        .build());
    }

}
