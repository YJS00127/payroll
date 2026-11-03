package payroll.exception;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    // 400 Bad Request 요청 데이터 이상 시 발생 예외
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handlerIllegalArgumentException(
            IllegalArgumentException e) {
        ErrorResponse response = new ErrorResponse("fail", e.getMessage());
        return ResponseEntity.badRequest().body(response);
    }

    // 404 Not Found Entity 존재하지 않을 시 발생 예외
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handlerEntityNotFoundException(
            EntityExistsException e){
        ErrorResponse response = new ErrorResponse("fail", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    // 400 Bad Request 입력값 검증 실패 시 발생 예외
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handlerMethodArgumentNotValidException(
            MethodArgumentNotValidException e){
        ErrorResponse response = new ErrorResponse("fail", e.getMessage());
        return ResponseEntity.badRequest().body(response);
    }

    // 400 Bad Request 입력값 -> DTO 변환 이상 시 발생 예외
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handlerHttpMessageNotReadableException(
            HttpMessageNotReadableException e){
        ErrorResponse response = new ErrorResponse("fail", "잘못된 요청입니다.");
        return ResponseEntity.badRequest().body(response);
    }

    // 409 Conflict 중복 등록 시 발생 예외
    @ExceptionHandler(EntityExistsException.class)
    public ResponseEntity<ErrorResponse> handlerEntityExistsException(
            EntityExistsException e){
        ErrorResponse response = new ErrorResponse("fail", e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handlerDataIntegrityViolationException(
            DataIntegrityViolationException e){
        ErrorResponse response = new ErrorResponse("fail", "DB 제약조건 위반");
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

}
