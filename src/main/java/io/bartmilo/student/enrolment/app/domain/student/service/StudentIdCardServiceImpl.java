package io.bartmilo.student.enrolment.app.domain.student.service;

import io.bartmilo.student.enrolment.app.domain.student.mapper.StudentIdCardMapper;
import io.bartmilo.student.enrolment.app.domain.student.mapper.StudentMapper;
import io.bartmilo.student.enrolment.app.domain.student.model.*;
import io.bartmilo.student.enrolment.app.domain.student.repository.StudentIdCardRepository;
import io.bartmilo.student.enrolment.app.domain.student.repository.StudentRepository;
import java.util.Random;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StudentIdCardServiceImpl implements StudentIdCardService {
  private static final Logger LOGGER = LoggerFactory.getLogger(StudentIdCardServiceImpl.class);

  private final StudentIdCardRepository studentIdCardRepository;
  private final StudentRepository studentRepository;
  private final Random random;
  private final StudentIdCardMapper studentIdCardMapper;
  private final StudentMapper studentMapper;

  public StudentIdCardServiceImpl(
      StudentIdCardRepository studentIdCardRepository,
      StudentRepository studentRepository,
      Random random,
      StudentIdCardMapper studentIdCardMapper,
      StudentMapper studentMapper) {
    this.studentIdCardRepository = studentIdCardRepository;
    this.studentRepository = studentRepository;
    this.random = random;
    this.studentIdCardMapper = studentIdCardMapper;
    this.studentMapper = studentMapper;
  }

  @Override
  @Transactional
  public StudentIdCardDto generateStudentIdCard(StudentDto studentDto) {
    var studentEntity = studentMapper.convertDtoToEntity(studentDto);
    studentRepository.saveAndFlush(studentEntity); // Ensures ID is generated
    var studentIdCardEntity =
        StudentIdCardEntity.builder()
            .cardNumber(generateCardNumber())
            .status(IdCardStatus.ACTIVE)
            .studentEntity(studentEntity)
            .build();
    studentIdCardEntity = studentIdCardRepository.save(studentIdCardEntity);
    LOGGER.info("STUDENT_ID_CARD_ENTITY: {}", studentIdCardEntity);
    return studentIdCardMapper.convertEntityToDto(studentIdCardEntity);
  }

  private String generateCardNumber() {
    return "CARD" + (random.nextInt() * 1000000);
  }
}
