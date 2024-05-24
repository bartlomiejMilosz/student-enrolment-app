package io.bartmilo.student.enrolment.app.domain.student.model;

import io.bartmilo.student.enrolment.app.domain.book.model.BookResponse;
import java.util.List;
import lombok.Builder;

@Builder
public record StudentResponse(
    Long id,
    String firstName,
    String lastName,
    String email,
    Integer age,
    StudentIdCardResponse studentIdCardResponse,
    List<BookResponse> bookResponseList) {}
