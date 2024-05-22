/*
package io.bartmilo.student.enrolment.app.domain.student.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import io.bartmilo.student.enrolment.app.TestDataUtil;
import io.bartmilo.student.enrolment.app.domain.student.model.StudentIdCardEntity;
import io.bartmilo.student.enrolment.app.domain.student.repository.StudentIdCardRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class StudentIdCardUnitTests {

  @Mock private StudentIdCardRepository studentIdCardRepositoryMock;

  private StudentIdCardService studentIdCardService;

  @BeforeEach
  void setUp() {
    this.studentIdCardService = new StudentIdCardServiceImpl(studentIdCardRepositoryMock);
  }

  @Test
  void whenStudentCreated_ThanGenerateIdCardWithActiveStatus() {
    var student = TestDataUtil.createSingleTestStudentEntity();
    student.setId(1L); // Assign an ID for reference
    var expectedIdCard =
        StudentIdCardEntity.builder()
            .cardNumber("CARD123456")
            .status(StudentIdCardEntity.CardStatus.ACTIVE)
            .studentEntity(student)
            .build();
    when(studentIdCardRepositoryMock.save(any(StudentIdCardEntity.class)))
        .thenReturn(expectedIdCard);

    var createdIdCard = studentIdCardService.generateStudentIdCard(student);

    verify(studentIdCardRepositoryMock, times(1)).save(any(StudentIdCardEntity.class));
    assertAll(
        () -> assertThat(createdIdCard).isEqualTo(expectedIdCard),
        () ->
            assertThat(createdIdCard.getStatus()).isEqualTo(StudentIdCardEntity.CardStatus.ACTIVE),
        () -> assertThat(createdIdCard.getStudentEntity()).isEqualTo(student));
  }
}
*/
