package io.bartmilo.student.enrolment.app.domain.student.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import io.bartmilo.student.enrolment.app.TestDataUtil;
import io.bartmilo.student.enrolment.app.domain.student.mapper.StudentMapper;
import io.bartmilo.student.enrolment.app.domain.student.model.IdCardStatus;
import io.bartmilo.student.enrolment.app.domain.student.model.StudentDto;
import io.bartmilo.student.enrolment.app.domain.student.model.StudentEntity;
import io.bartmilo.student.enrolment.app.domain.student.model.StudentIdCardDto;
import io.bartmilo.student.enrolment.app.domain.student.repository.StudentIdCardRepository;
import io.bartmilo.student.enrolment.app.domain.student.repository.StudentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class StudentIdCardIntegrationTests {

  @Autowired
  private StudentIdCardService studentIdCardService;

  @Autowired
  private StudentIdCardRepository studentIdCardRepository;

  @Autowired
  private StudentRepository studentRepository;

  @Autowired
  private StudentMapper studentMapper;

  @Test
  void whenGenerateStudentIdCard_ThenPersistIdCardCorrectly() {
    var studentDto = TestDataUtil.createSingleTestStudentDto();
    var studentEntity = studentMapper.convertDtoToEntity(studentDto);
    studentEntity = studentRepository.save(studentEntity);

    var savedStudentDto = studentMapper.convertEntityToDto(studentEntity);

    var createdIdCardDto = studentIdCardService.generateStudentIdCard(savedStudentDto);
    var fetchedIdCard = studentIdCardRepository.findById(createdIdCardDto.id()).orElse(null);

    var finalStudentEntity = studentEntity;
    assertAll(
        () -> assertThat(createdIdCardDto).isNotNull(),
        () -> assertThat(createdIdCardDto.cardNumber()).startsWith("CARD"),
        () -> assertThat(createdIdCardDto.status()).isEqualTo(IdCardStatus.ACTIVE),
        () -> assertThat(createdIdCardDto.studentId()).isEqualTo(finalStudentEntity.getId()));

    assertAll(
        () -> assertThat(fetchedIdCard).isNotNull(),
        () -> assertThat(fetchedIdCard.getCardNumber()).isEqualTo(createdIdCardDto.cardNumber()),
        () -> assertThat(fetchedIdCard.getStatus()).isEqualTo(createdIdCardDto.status()),
        () -> assertThat(fetchedIdCard.getStudentEntity().getId()).isEqualTo(finalStudentEntity.getId()));
  }

}
