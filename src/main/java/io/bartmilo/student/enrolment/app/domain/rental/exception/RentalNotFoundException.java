package io.bartmilo.student.enrolment.app.domain.rental.exception;

public class RentalNotFoundException extends RuntimeException {
  public RentalNotFoundException(String message) {
    super(message);
  }
}
