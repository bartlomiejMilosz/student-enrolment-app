package io.bartmilo.student.enrolment.app.domain.student.model;

import io.bartmilo.student.enrolment.app.domain.book.model.BookDto;
import java.util.List;
import lombok.Builder;

@Builder
public record StudentDto(
    Long id,
    String firstName,
    String lastName,
    String email,
    Integer age,
    StudentIdCardDto studentIdCardDto,
    List<BookDto> bookDtoList) {}
