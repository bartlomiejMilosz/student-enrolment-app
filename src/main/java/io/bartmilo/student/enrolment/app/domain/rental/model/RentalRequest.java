package io.bartmilo.student.enrolment.app.domain.rental.model;

import java.time.LocalDateTime;
import lombok.*;

@Builder
public record RentalRequest(
    Long id,
    LocalDateTime rentedAt,
    LocalDateTime dueDate,
    LocalDateTime returnedAt,
    Long studentId,
    Long bookId) {}
