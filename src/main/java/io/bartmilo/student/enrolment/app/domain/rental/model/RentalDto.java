package io.bartmilo.student.enrolment.app.domain.rental.model;

import java.time.LocalDateTime;
import lombok.*;

@Getter
@Setter
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
