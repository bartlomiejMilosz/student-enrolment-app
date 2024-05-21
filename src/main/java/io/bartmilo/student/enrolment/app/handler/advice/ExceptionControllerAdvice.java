package io.bartmilo.student.enrolment.app.handler.advice;

import io.bartmilo.student.enrolment.app.domain.student.StudentController;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice(assignableTypes = {StudentController.class})
public class ExceptionControllerAdvice {

  private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionControllerAdvice.class);

  private Map<String, Object> getResponseBody(
      HttpServletRequest request, HttpStatus httpStatus, String massage) {
    Map<String, Object> body = new LinkedHashMap<>();

    body.put("timestamp", LocalDateTime.now());
    body.put("status", httpStatus.value());
    body.put("error", httpStatus.getReasonPhrase());
    body.put("message", massage);
    body.put("path", request.getRequestURI());
    return body;
  }

  @ExceptionHandler(EntityNotFoundException.class)
  public ResponseEntity<Object> handleEntityNotFound(
      HttpServletRequest request, EntityNotFoundException e) {
    LOGGER.error("Entity not found: {}", e.getMessage());

    Map<String, Object> body = getResponseBody(request, HttpStatus.NOT_FOUND, e.getMessage());
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<Object> handleMessageNotReadableException(
      HttpServletRequest request, HttpMessageNotReadableException e) {
    LOGGER.error("Message not readable: {}", e.getMessage());

    Map<String, Object> body =
        getResponseBody(request, HttpStatus.BAD_REQUEST, "The request message is not readable.");
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<Object> handleAllException(HttpServletRequest request, Exception e) {
    LOGGER.error("An unexpected error occurred: {}", e.getMessage());

    Map<String, Object> body =
        getResponseBody(request, HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred.");
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
  }
}
