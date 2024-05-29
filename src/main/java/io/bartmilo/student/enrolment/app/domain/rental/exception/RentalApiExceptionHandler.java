package io.bartmilo.student.enrolment.app.domain.rental.exception;

import io.bartmilo.student.enrolment.app.domain.rental.RentalController;
import jakarta.servlet.http.HttpServletRequest;
import java.time.ZonedDateTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice(assignableTypes = {RentalController.class})
public class RentalApiExceptionHandler {

  @ExceptionHandler(RentalNotFoundException.class)
  public ResponseEntity<RentalExceptionResponse> handleRentalNotFoundException(
      HttpServletRequest request, RentalNotFoundException ex) {
    LOGGER.error("Handling RentalNotFoundException for request: {}", request.getRequestURI());
    LOGGER.error("Rental not found: {}", ex.getMessage());
    var rentalExceptionResponse = getRentalExceptionResponse(HttpStatus.NOT_FOUND, request, ex);
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(rentalExceptionResponse);
  }

  private RentalExceptionResponse getRentalExceptionResponse(
      HttpStatus httpStatus, HttpServletRequest request, Exception ex) {
    return RentalExceptionResponse.builder()
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
