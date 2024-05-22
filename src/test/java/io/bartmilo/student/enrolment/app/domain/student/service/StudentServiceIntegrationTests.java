/*
package io.bartmilo.student.enrolment.app.domain.student.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import io.bartmilo.student.enrolment.app.TestDataUtil;
import io.bartmilo.student.enrolment.app.domain.student.model.StudentEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;

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
    var studentEntity = TestDataUtil.createSingleTestStudentEntity();
    studentService.save(studentEntity);

    var result = studentService.findById(studentEntity.getId());

    assertAll(
        () -> assertThat(result).isPresent(), () -> assertThat(result).contains(studentEntity));
  }

  @Test
  void whenListOfStudentsIsSaved_ThanCanBeRecalled() {
    var studentEntityList = TestDataUtil.createListOfTestStudentEntities();
    studentEntityList.forEach(studentService::save);

    int page = 0;
    int size = 3;
    var pageable = PageRequest.of(page, size);

    var result = studentService.findAll(pageable);

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
          var studentFromPage = result.getContent().get(0);
          var studentFromList = studentEntityList.get(0);
          assertThat(studentFromPage).isEqualTo(studentFromList);
        });
  }

  @Test
  void whenFindById_ThenReturnStudent() {
    var student = TestDataUtil.createSingleTestStudentEntity();
    studentService.save(student);

    var foundStudent = studentService.findById(student.getId());

    assertThat(foundStudent).isPresent().contains(student);
  }

  @Test
  void whenCheckExistence_ThenConfirm() {
    var student = TestDataUtil.createSingleTestStudentEntity();
    studentService.save(student);

    boolean exists = !studentService.isExists(student.getId());

    assertTrue(exists);
  }

  @Test
  void whenPartialUpdate_ThenUpdateFields() {
    var student = TestDataUtil.createSingleTestStudentEntity();
    student = studentService.save(student);
    var updatedInfo = new StudentEntity();
    updatedInfo.setFirstName("UpdatedStudentName");

    var updatedStudent = studentService.partialUpdate(student.getId(), updatedInfo);

    assertThat(updatedStudent.getFirstName()).isEqualTo("UpdatedStudentName");
  }

  @Test
  void whenDelete_ThenStudentIsNotPresent() {
    var student = TestDataUtil.createSingleTestStudentEntity();
    student = studentService.save(student);

    studentService.delete(student.getId());

    var result = studentService.findById(student.getId());
    assertThat(result).isEmpty();
  }
}
*/
