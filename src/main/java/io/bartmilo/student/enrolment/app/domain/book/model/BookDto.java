package io.bartmilo.student.enrolment.app.domain.book.model;

import java.time.LocalDateTime;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
public class BookDto {
  private Long id;
  private String bookAuthor;
  private String title;
  private String isbn;
  private LocalDateTime createdAt;
  private Integer stock;
}
