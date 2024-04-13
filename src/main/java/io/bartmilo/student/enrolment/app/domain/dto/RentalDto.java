package io.bartmilo.student.enrolment.app.domain.dto;

import io.bartmilo.student.enrolment.app.domain.entity.BookEntity;
import io.bartmilo.student.enrolment.app.domain.entity.StudentEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RentalDto {

    private Long id;

    private LocalDateTime rentedAt;

    private LocalDateTime dueDate;

    private LocalDateTime returnedAt;

    private Long studentId;

    private Long bookId;

}
