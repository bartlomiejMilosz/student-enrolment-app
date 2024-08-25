package io.bartmilo.student.enrolment.app.domain.student.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import io.bartmilo.student.enrolment.app.TestDataUtil;
import io.bartmilo.student.enrolment.app.domain.student.mapper.StudentIdCardMapper;
import io.bartmilo.student.enrolment.app.domain.student.mapper.StudentMapper;
import io.bartmilo.student.enrolment.app.domain.student.model.*;
import io.bartmilo.student.enrolment.app.domain.student.repository.StudentIdCardRepository;
import io.bartmilo.student.enrolment.app.domain.student.repository.StudentRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Random;

@ExtendWith(MockitoExtension.class)
class StudentIdCardUnitTests {

  @Mock
  private StudentIdCardRepository studentIdCardRepository;
  @Mock
  private StudentRepository studentRepository;
  @Mock
  private StudentIdCardMapper studentIdCardMapper;
  @Mock
  private StudentMapper studentMapper;
  @Mock
  private Random random;

  @InjectMocks
  private StudentIdCardServiceImpl studentIdCardService;

  @BeforeEach
  void setUp() {
    when(random.nextInt()).thenReturn(123456); // For consistent card number generation
  }

  @Test
  void generateStudentIdCard_ShouldCreateActiveCardForNewStudent() {
    var studentDto = TestDataUtil.createSingleTestStudentDto();
    var studentEntity = TestDataUtil.createSingleTestStudentEntity();
    var studentIdCardEntity = TestDataUtil.createSingleTestStudentIdCardEntity(1L);
    var studentIdCardDto = TestDataUtil.createSingleTestStudentIdCardDto(1L);

    when(studentMapper.convertDtoToEntity(any(StudentDto.class))).thenReturn(studentEntity);
    when(studentRepository.saveAndFlush(any(StudentEntity.class))).thenReturn(studentEntity);
    when(studentIdCardRepository.save(any(StudentIdCardEntity.class))).thenReturn(studentIdCardEntity);
    when(studentIdCardMapper.convertEntityToDto(any(StudentIdCardEntity.class))).thenReturn(studentIdCardDto);

    var result = studentIdCardService.generateStudentIdCard(studentDto);

    verify(studentRepository).saveAndFlush(studentEntity);
    verify(studentIdCardRepository).save(any(StudentIdCardEntity.class));
    assertThat(result).isEqualTo(studentIdCardDto);
    assertThat(result.status()).isEqualTo(IdCardStatus.ACTIVE);
  }
}
