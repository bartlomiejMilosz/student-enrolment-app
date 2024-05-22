package io.bartmilo.student.enrolment.app.domain.book.exception;

public class BookNotFoundException extends RuntimeException {
  public BookNotFoundException(String message) {
    super(message);
  }
}
