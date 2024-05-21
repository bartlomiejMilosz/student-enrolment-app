package io.bartmilo.student.enrolment.app.service;

import io.bartmilo.student.enrolment.app.TestDataUtil;
import io.bartmilo.student.enrolment.app.domain.student.model.StudentIdCardEntity;
import io.bartmilo.student.enrolment.app.domain.student.repository.StudentIdCardRepository;
import io.bartmilo.student.enrolment.app.domain.student.repository.StudentRepository;
import io.bartmilo.student.enrolment.app.domain.student.service.StudentIdCardService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class StudentIdCardIntegrationTests {

    @Autowired
    private StudentIdCardService studentIdCardService;

    @Autowired
    private StudentIdCardRepository studentIdCardRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Test
    void whenGenerateStudentIdCard_ThenPersistIdCardCorrectly() {
        // Given
        var studentEntity = TestDataUtil.createSingleTestStudentEntity();
        studentRepository.save(studentEntity);

        // When
        var createdIdCard = studentIdCardService.generateStudentIdCard(studentEntity);
        var fetchedIdCard = studentIdCardRepository.findById(createdIdCard.getId()).orElse(null);

        // Then
        assertAll(
                () -> assertThat(createdIdCard).isNotNull(),
                () -> assertThat(createdIdCard.getCardNumber()).startsWith("CARD"),
                () -> assertThat(createdIdCard.getStatus()).isEqualTo(StudentIdCardEntity.CardStatus.ACTIVE),
                () -> assertThat(createdIdCard.getStudentEntity()).isEqualTo(studentEntity)
        );

        assertAll(
                () -> assertThat(fetchedIdCard).isNotNull(),
                () -> assertThat(fetchedIdCard).isEqualTo(createdIdCard)
        );
    }

}
