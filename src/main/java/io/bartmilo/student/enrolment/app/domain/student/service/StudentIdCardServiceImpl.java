package io.bartmilo.student.enrolment.app.domain.student.service;

import io.bartmilo.student.enrolment.app.domain.student.model.*;
import io.bartmilo.student.enrolment.app.domain.student.repository.StudentIdCardRepository;
import io.bartmilo.student.enrolment.app.util.DomainMapper;
import java.util.Random;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StudentIdCardServiceImpl implements StudentIdCardService {
  private final StudentIdCardRepository studentIdCardRepository;
  private final Random random;
  private final DomainMapper domainMapper;

  public StudentIdCardServiceImpl(
      StudentIdCardRepository studentIdCardRepository, Random random, DomainMapper domainMapper) {
    this.studentIdCardRepository = studentIdCardRepository;
    this.random = random;
    this.domainMapper = domainMapper;
  }

  @Override
  @Transactional
  public StudentIdCardDto generateStudentIdCard(StudentDto studentDto) {
    var studentEntity = domainMapper.convertEntityToDto(studentDto, StudentEntity.class);
    var studentIdCard =
        StudentIdCardEntity.builder()
            .cardNumber(generateCardNumber())
            .status(IdCardStatus.ACTIVE)
            .studentEntity(studentEntity)
            .build();

    var savedStudentIdCardEntity = studentIdCardRepository.save(studentIdCard);
    return domainMapper.convertEntityToDto(savedStudentIdCardEntity, StudentIdCardDto.class);
  }

  private String generateCardNumber() {
    return "CARD" + (random.nextInt() * 1000000);
  }
}
