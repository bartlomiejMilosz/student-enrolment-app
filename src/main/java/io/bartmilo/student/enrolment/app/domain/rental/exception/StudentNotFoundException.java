package io.bartmilo.student.enrolment.app.domain.rental.exception;

public class StudentNotFoundException extends RuntimeException{
    public StudentNotFoundException(String message) {
        super(message);
    }
}
