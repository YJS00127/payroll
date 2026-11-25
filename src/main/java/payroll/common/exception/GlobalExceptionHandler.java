package payroll.common.exception;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    // 400 Bad Request 요청 데이터 이상 시 발생 예외
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handlerIllegalArgumentException(
            IllegalArgumentException e) {

        log.error("400 Bad Request : {}", e.getMessage());
        ErrorResponse response = new ErrorResponse("fail", e.getMessage());
        return ResponseEntity.badRequest().body(response);
    }

    // 404 Not Found Entity 존재하지 않을 시 발생 예외
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handlerEntityNotFoundException(
            EntityExistsException e){

        log.error("404 Not Found : {}", e.getMessage());
        ErrorResponse response = new ErrorResponse("fail", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    // 400 Bad Request 입력값 검증 실패 시 발생 예외
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handlerMethodArgumentNotValidException(
            MethodArgumentNotValidException e){

        String message = e.getBindingResult()
                .getFieldErrors()
                .get(0)
                .getDefaultMessage();

        log.error("400 Bad Request : 입력값 검증 실패", e);
        ErrorResponse response = new ErrorResponse("fail", message);
        return ResponseEntity.badRequest().body(response);
    }

    // 400 Bad Request 입력값 -> DTO 변환 이상 시 발생 예외
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handlerHttpMessageNotReadableException(
            HttpMessageNotReadableException e){

        log.error("400 Bad Request : {}", e.getMessage());
        ErrorResponse response = new ErrorResponse("fail", "잘못된 요청입니다.");
        return ResponseEntity.badRequest().body(response);
    }

    // 409 Conflict 중복 등록 시 발생 예외
    @ExceptionHandler(EntityExistsException.class)
    public ResponseEntity<ErrorResponse> handlerEntityExistsException(
            EntityExistsException e){

        log.error("DB제약 조건 위반", e);
        ErrorResponse response = new ErrorResponse("fail", e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handlerDataIntegrityViolationException(
            DataIntegrityViolationException e){

        log.error("DB제약조건 위반", e);
        ErrorResponse response = new ErrorResponse("fail", "DB 제약조건 위반");
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(EmployeeInDepartmentException.class)
    public ResponseEntity<ErrorResponse> handlerEmployeeInDepartmentException(
            EmployeeInDepartmentException e){

        log.error("409 Conflict : {}", e.getMessage());
        ErrorResponse response = new ErrorResponse("fail", e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<ErrorResponse> handlerIOException(IOException e){

        log.error("파일 입출력 오류 : {}", e.getMessage());
        ErrorResponse response = new ErrorResponse("fail", e.getMessage());
        return ResponseEntity.internalServerError().
                body(new ErrorResponse("fail", "파일 처리 중 오류가 발생했습니다"));
    }

    private String validationError(BindingResult bindingResult){
        return bindingResult.getFieldErrors()
                .stream()
                .map(error -> error.getDefaultMessage())
                .collect(Collectors.joining("\n"));
    }


}
