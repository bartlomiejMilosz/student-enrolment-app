package io.bartmilo.student.enrolment.app.domain.student.service;

import io.bartmilo.student.enrolment.app.domain.student.model.StudentDto;
import io.bartmilo.student.enrolment.app.domain.student.model.StudentIdCardDto;

public interface StudentIdCardService {

  /**
   * Generates a new ID card for the given student. This method is responsible for creating a new
   * {@link StudentIdCardDto} that represents an ID card issued to a student. The ID card contains
   * details such as the card's unique identifier, issue date, and status.
   *
   * @param studentDto the {@link StudentDto} for whom the ID card is to be generated
   * @return a {@link StudentIdCardDto} representing the newly created student ID card, which
   *     includes all relevant details such as card number, issue date, and status
   */
  StudentIdCardDto generateStudentIdCard(StudentDto studentDto);
}
