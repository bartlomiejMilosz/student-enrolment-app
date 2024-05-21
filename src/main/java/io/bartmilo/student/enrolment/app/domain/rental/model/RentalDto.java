package io.bartmilo.student.enrolment.app.domain.rental.model;

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
