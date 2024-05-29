package io.bartmilo.student.enrolment.app.handler.advice;

import io.bartmilo.student.enrolment.app.domain.book.BookController;
import io.bartmilo.student.enrolment.app.domain.rental.RentalController;
import io.bartmilo.student.enrolment.app.domain.student.StudentController;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import java.time.ZonedDateTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice(
    assignableTypes = {StudentController.class, BookController.class, RentalController.class})
public class GlobalApiExceptionHandler {

  @ExceptionHandler(EntityNotFoundException.class)
  public ResponseEntity<CommonExceptionResponse> handleEntityNotFoundException(
      HttpServletRequest request, EntityNotFoundException ex) {
    LOGGER.error("Handling EntityNotFoundException for request: {}", request.getRequestURI());
    LOGGER.error("Entity not found: {}", ex.getMessage());
    var commonExceptionResponse = getCommonExceptionResponse(HttpStatus.NOT_FOUND, request, ex);
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(commonExceptionResponse);
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<CommonExceptionResponse> handleIllegalArgumentException(
      HttpServletRequest request, EntityNotFoundException ex) {
    LOGGER.error("Handling IllegalArgumentException for request: {}", request.getRequestURI());
    LOGGER.error("IllegalArgumentException: {}", ex.getMessage());
    var commonExceptionResponse = getCommonExceptionResponse(HttpStatus.BAD_REQUEST, request, ex);
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(commonExceptionResponse);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<Object> handleUnspecifiedException(
      HttpServletRequest request, Exception ex) {
    LOGGER.error("Handling unspecified Exception for request: {}", request.getRequestURI());
    LOGGER.error("An unexpected error occurred: {}", ex.getMessage());
    var commonExceptionResponse =
        getCommonExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR, request, ex);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(commonExceptionResponse);
  }

  private CommonExceptionResponse getCommonExceptionResponse(
      HttpStatus httpStatus, HttpServletRequest request, Exception ex) {
    return CommonExceptionResponse.builder()
        .message(ex.getMessage())
        .cause(ex.getCause() != null ? ex.getCause().getMessage() : "No cause provided")
        .status(httpStatus)
        .timestamp(ZonedDateTime.now())
        .path(request.getRequestURI())
        .httpMethod(request.getMethod())
        .userAgent(request.getHeader("User-Agent"))
        .build();
  }
}
