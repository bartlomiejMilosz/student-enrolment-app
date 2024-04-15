package io.bartmilo.student.enrolment.app.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentDto {

    private Long id;

    private String firstName;

    private String lastName;

    private String email;

    private Integer age;

    private StudentIdCardDto studentIdCard;

    private List<BookDto> books;
}
