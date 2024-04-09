package io.bartmilo.student.enrolment.app.domain.dto;

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

    private String bookName;

    private String bookAuthor;

    private LocalDateTime createdAt;
}
