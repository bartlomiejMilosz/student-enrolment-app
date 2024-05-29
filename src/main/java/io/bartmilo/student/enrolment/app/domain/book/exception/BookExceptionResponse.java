package io.bartmilo.student.enrolment.app.domain.book.exception;

import java.time.ZonedDateTime;
import lombok.*;
import org.springframework.http.HttpStatus;

/** Represents details of an error condition */
@Getter
@Setter
@ToString
@Builder
public class BookExceptionResponse {
  private String message; // Detailed error message
  private String cause; // Cause of the error
  private HttpStatus status;
  private ZonedDateTime timestamp;
  private String path; // Request path where the error occurred
  private String httpMethod;
  private String userAgent;

  public BookExceptionResponse(
      String message,
      String cause,
      HttpStatus status,
      ZonedDateTime timestamp,
      String path,
      String httpMethod,
      String userAgent) {
    this.message = message;
    this.cause = cause != null ? cause : "No cause";
    this.status = status;
    this.timestamp = timestamp;
    this.path = path;
    this.httpMethod = httpMethod;
    this.userAgent = userAgent;
  }
}
