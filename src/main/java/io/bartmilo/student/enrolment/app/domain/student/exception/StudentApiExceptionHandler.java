package io.bartmilo.student.enrolment.app.domain.student.exception;

import io.bartmilo.student.enrolment.app.domain.student.StudentController;
import jakarta.servlet.http.HttpServletRequest;
import java.time.ZonedDateTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice(assignableTypes = {StudentController.class})
public class StudentApiExceptionHandler {

  @ExceptionHandler(StudentNotFoundException.class)
  public ResponseEntity<StudentExceptionResponse> handleStudentNotFoundException(
      HttpServletRequest request, StudentNotFoundException ex) {
    LOGGER.error("Handling StudentNotFoundException for request: {}", request.getRequestURI());
    LOGGER.error("Student not found: {}", ex.getMessage());
    var studentExceptionResponse = getStudentExceptionResponse(HttpStatus.NOT_FOUND, request, ex);
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(studentExceptionResponse);
  }

  private StudentExceptionResponse getStudentExceptionResponse(
      HttpStatus httpStatus, HttpServletRequest request, Exception ex) {
    return StudentExceptionResponse.builder()
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
