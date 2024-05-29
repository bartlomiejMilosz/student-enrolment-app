package io.bartmilo.student.enrolment.app.domain.book.exception;

import io.bartmilo.student.enrolment.app.domain.book.BookController;
import jakarta.servlet.http.HttpServletRequest;
import java.time.ZonedDateTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice(assignableTypes = {BookController.class})
public class BookApiExceptionHandler {

  @ExceptionHandler(BookNotFoundException.class)
  public ResponseEntity<BookExceptionResponse> handleBookNotFoundException(
      HttpServletRequest request, BookNotFoundException ex) {
    LOGGER.error("Handling BookNotFoundException for request: {}", request.getRequestURI());
    LOGGER.error("Book not found: {}", ex.getMessage());
    var bookExceptionResponse = getBookExceptionResponse(HttpStatus.NOT_FOUND, request, ex);
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(bookExceptionResponse);
  }

  private BookExceptionResponse getBookExceptionResponse(
      HttpStatus httpStatus, HttpServletRequest request, Exception ex) {
    return BookExceptionResponse.builder()
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
