package io.bartmilo.student.enrolment.app.service;

import io.bartmilo.student.enrolment.app.TestDataUtil;
import io.bartmilo.student.enrolment.app.domain.entity.StudentEntity;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class StudentServiceIntegrationTests {
    private final StudentService studentService;

    @Autowired
    StudentServiceIntegrationTests(StudentService studentService) {
        this.studentService = studentService;
    }

    @Test
    void whenStudentIsSaved_ThanCanBeRecalled() {
        // Given
        var studentEntity = TestDataUtil.createSingleTestStudentEntity();
        studentService.save(studentEntity);

        // When
        var result = studentService.findById(studentEntity.getId());

        // Then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(studentEntity);
    }

    @Test
    void whenListOfStudentsIsSaved_ThanCanBeRecalled() {
        // Given
        var studentEntityList = TestDataUtil.createListOfTestStudentEntities();
        studentEntityList.forEach(studentService::save);

        int page = 0;
        int size = 3;
        var pageable = PageRequest.of(page, size);

        // When
        var result = studentService.findAll(pageable);

        // Then
        assertAll(
                // Check that the page content has the correct number of students
                () -> assertThat(result.getContent()).hasSize(size),

                // Check the page number
                () -> assertThat(result.getNumber()).isEqualTo(page),

                // Check the page size
                () -> assertThat(result.getSize()).isEqualTo(size),

                // Check the total number of elements
                () -> assertThat(result.getTotalElements()).isEqualTo(studentEntityList.size()),

                // Check the first student in the page against the first student in the list
                () -> {
                    StudentEntity studentFromPage = result.getContent().get(0);
                    StudentEntity studentFromList = studentEntityList.get(0);
                    assertThat(studentFromPage).isEqualTo(studentFromList);
                }
        );
    }

    @Test
    void whenFindById_ThenReturnStudent() {
        // Given
        var student = TestDataUtil.createSingleTestStudentEntity();
        studentService.save(student);

        // When
        var foundStudent = studentService.findById(student.getId());

        // Then
        assertThat(foundStudent).isPresent().contains(student);
    }

    @Test
    void whenCheckExistence_ThenConfirm() {
        // Given
        var student = TestDataUtil.createSingleTestStudentEntity();
        studentService.save(student);

        // When
        boolean exists = !studentService.isExists(student.getId());

        // Then
        assertTrue(exists);
    }

    @Test
    void whenPartialUpdate_ThenUpdateFields() {
        // Given
        var student = TestDataUtil.createSingleTestStudentEntity();
        student = studentService.save(student);
        var updatedInfo = new StudentEntity();
        updatedInfo.setFirstName("UpdatedStudentName");

        // When
        var updatedStudent = studentService.partialUpdate(student.getId(), updatedInfo);

        // Then
        assertThat(updatedStudent.getFirstName()).isEqualTo("UpdatedStudentName");
    }

    @Test
    void whenDelete_ThenStudentIsNotPresent() {
        // Given
        var student = TestDataUtil.createSingleTestStudentEntity();
        student = studentService.save(student);

        // When
        studentService.delete(student.getId());

        // Then
        var result = studentService.findById(student.getId());
        assertThat(result).isEmpty();
    }
}