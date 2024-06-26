package io.bartmilo.student.enrolment.app.domain.book.model;

import java.time.LocalDateTime;
import lombok.*;

@Builder
public record BookResponse(
    Long id,
    String bookAuthor,
    String title,
    String isbn,
    LocalDateTime createdAt,
    Integer stock) {}
