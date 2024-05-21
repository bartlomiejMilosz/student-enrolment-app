package io.bartmilo.student.enrolment.app.domain.book.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookDto {

    private Long id;

    private String bookAuthor;

    private String title;

    private String isbn;

    private LocalDateTime createdAt;

    private Integer stock;
}
