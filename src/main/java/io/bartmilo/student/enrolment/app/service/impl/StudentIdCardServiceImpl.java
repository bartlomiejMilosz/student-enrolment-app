package io.bartmilo.student.enrolment.app.service.impl;

import io.bartmilo.student.enrolment.app.domain.entity.StudentEntity;
import io.bartmilo.student.enrolment.app.domain.entity.StudentIdCardEntity;
import io.bartmilo.student.enrolment.app.repository.StudentIdCardRepository;
import io.bartmilo.student.enrolment.app.service.StudentIdCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StudentIdCardServiceImpl implements StudentIdCardService {
    private final StudentIdCardRepository studentIdCardRepository;

    @Autowired
    public StudentIdCardServiceImpl(StudentIdCardRepository studentIdCardRepository) {
        this.studentIdCardRepository = studentIdCardRepository;
    }

    @Override
    @Transactional
    public StudentIdCardEntity generateStudentIdCard(StudentEntity studentEntity) {
        StudentIdCardEntity studentIdCard = StudentIdCardEntity.builder()
                .cardNumber(generateCardNumber())
                .status(StudentIdCardEntity.CardStatus.ACTIVE)
                .studentEntity(studentEntity)
                .build();

        return studentIdCardRepository.save(studentIdCard);
    }

    private String generateCardNumber() {
        // Implement your logic to generate a unique card number here
        return "CARD" + (int)(Math.random() * 1000000);
    }
}
