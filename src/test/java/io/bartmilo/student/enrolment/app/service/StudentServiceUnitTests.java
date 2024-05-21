package io.bartmilo.student.enrolment.app.service;

import io.bartmilo.student.enrolment.app.TestDataUtil;
import io.bartmilo.student.enrolment.app.domain.student.model.StudentEntity;
import io.bartmilo.student.enrolment.app.domain.student.model.StudentIdCardEntity;
import io.bartmilo.student.enrolment.app.domain.student.repository.StudentRepository;
import io.bartmilo.student.enrolment.app.domain.student.service.StudentIdCardService;
import io.bartmilo.student.enrolment.app.domain.student.service.StudentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class StudentServiceUnitTests {

    @MockBean
    private StudentRepository studentRepositoryMock;

    @MockBean
    private StudentIdCardService studentIdCardServiceMock;

    @Autowired
    private StudentService studentService;

    @Test
    void whenStudentIsSaved_ThanCanBeRecalled() {
        // Given
        var studentEntity = TestDataUtil.createSingleTestStudentEntity();
        var idCard = new StudentIdCardEntity(); // Create a test ID card
        when(studentIdCardServiceMock.generateStudentIdCard(studentEntity)).thenReturn(idCard);
        when(studentRepositoryMock.save(studentEntity)).thenReturn(studentEntity);

        // When
        var savedStudent = studentService.save(studentEntity);

        // Then
        verify(studentRepositoryMock, times(1)).save(studentEntity);

        assertAll(
                () -> assertThat(savedStudent.getStudentIdCardEntity()).isEqualTo(idCard),
                () -> assertThat(savedStudent).isEqualTo(studentEntity)
        );
    }

    @Test
    void whenListOfStudentsIsSaved_ThanCanBeRecalled() {
        // Given
        var studentEntityList = TestDataUtil.createListOfTestStudentEntities();
        for (StudentEntity student : studentEntityList) {
            when(studentRepositoryMock.save(student)).thenReturn(student);
        }

        // When
        studentEntityList.forEach(studentService::save);

        // Then
        for (StudentEntity student : studentEntityList) {
            verify(studentRepositoryMock).save(student);
            assertThat(studentService.save(student)).isEqualTo(student);
        }
    }

    @Test
    void whenFindAll_ThenReturnPageOfStudents() {
        // Given
        var pageable = PageRequest.of(0, 10);
        var students = TestDataUtil.createListOfTestStudentEntities();
        var expectedPage = new PageImpl<>(students, pageable, students.size());
        when(studentRepositoryMock.findAll(pageable)).thenReturn(expectedPage);

        // When
        var result = studentService.findAll(pageable);

        // Then
        assertAll(
                () -> assertThat(result.getContent()).isEqualTo(students),
                () -> assertThat(result.getTotalElements()).isEqualTo(students.size())
        );
    }

    @Test
    void whenFindById_ThenReturnStudent() {
        // Given
        var student = TestDataUtil.createSingleTestStudentEntity();
        when(studentRepositoryMock.findById(student.getId())).thenReturn(Optional.of(student));

        // When
        var result = studentService.findById(student.getId());

        // Then
        assertThat(result).isPresent().contains(student);
    }

    @Test
    void whenCheckIfStudentExists_ThenReturnTrueOrFalse() {
        // Given
        Long studentId = 1L;
        when(studentRepositoryMock.existsById(studentId)).thenReturn(true);

        // When
        boolean exists = !studentService.isExists(studentId);

        // Then
        assertThat(exists).isTrue();
    }

    @Test
    void whenPartialUpdate_ThenStudentIsUpdated() {
        // Given
        Long studentId = 1L;
        var student = TestDataUtil.createSingleTestStudentEntity();
        student.setId(studentId);
        var updateInfo = new StudentEntity();
        updateInfo.setFirstName("UpdatedFirstName");

        when(studentRepositoryMock.findById(studentId)).thenReturn(Optional.of(student));
        when(studentRepositoryMock.save(any(StudentEntity.class))).thenReturn(student);

        // When
        var updatedStudent = studentService.partialUpdate(studentId, updateInfo);

        // Then
        verify(studentRepositoryMock).save(student); // Verify save was called on the original student
        assertThat(updatedStudent.getFirstName()).isEqualTo(updateInfo.getFirstName());
    }

    @Test
    void whenDelete_ThenRepositoryDeleteIsCalled() {
        // Given
        Long studentId = 1L;
        doNothing().when(studentRepositoryMock).deleteById(studentId);

        // When
        studentService.delete(studentId);

        // Then
        verify(studentRepositoryMock, times(1)).deleteById(studentId);
        assertThat(studentService.findById(studentId)).isEmpty();
    }

}
