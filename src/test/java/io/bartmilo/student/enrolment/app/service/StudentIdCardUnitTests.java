package io.bartmilo.student.enrolment.app.service;

import io.bartmilo.student.enrolment.app.TestDataUtil;
import io.bartmilo.student.enrolment.app.domain.student.model.StudentIdCardEntity;
import io.bartmilo.student.enrolment.app.domain.student.repository.StudentIdCardRepository;
import io.bartmilo.student.enrolment.app.domain.student.service.StudentIdCardService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class StudentIdCardUnitTests {

    @MockBean
    private StudentIdCardRepository studentIdCardRepositoryMock;

    @Autowired
    private StudentIdCardService studentIdCardService;

    @Test
    void whenStudentCreated_ThanGenerateIdCardWithActiveStatus() {
        // Given
        var student = TestDataUtil.createSingleTestStudentEntity();
        student.setId(1L);  // Assign an ID for reference
        var expectedIdCard = StudentIdCardEntity.builder()
                .cardNumber("CARD123456")
                .status(StudentIdCardEntity.CardStatus.ACTIVE)
                .studentEntity(student)
                .build();
        when(studentIdCardRepositoryMock.save(any(StudentIdCardEntity.class))).thenReturn(expectedIdCard);

        // When
        var createdIdCard = studentIdCardService.generateStudentIdCard(student);

        // Then
        verify(studentIdCardRepositoryMock, times(1)).save(any(StudentIdCardEntity.class));
        assertAll(
                () -> assertThat(createdIdCard).isEqualTo(expectedIdCard),
                () -> assertThat(createdIdCard.getStatus()).isEqualTo(StudentIdCardEntity.CardStatus.ACTIVE),
                () -> assertThat(createdIdCard.getStudentEntity()).isEqualTo(student)
        );

    }
}
