package io.bartmilo.student.enrolment.app.domain.student.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import io.bartmilo.student.enrolment.app.TestDataUtil;
import io.bartmilo.student.enrolment.app.domain.student.exception.StudentNotFoundException;
import io.bartmilo.student.enrolment.app.domain.student.mapper.StudentMapper;
import io.bartmilo.student.enrolment.app.domain.student.model.StudentDto;
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
  private final StudentMapper studentMapper;

  @Autowired
  StudentServiceIntegrationTests(StudentService studentService, StudentMapper studentMapper) {
    this.studentService = studentService;
    this.studentMapper = studentMapper;
  }

  @Test
  void whenStudentIsSaved_ThenCanBeRecalled() {
    var studentDto = TestDataUtil.createSingleTestStudentDto();
    var savedStudentDto = studentService.save(studentDto);

    var result = studentService.findById(savedStudentDto.id());
    assertNotNull(result);

    assertAll(
        () -> assertThat(result.id()).isEqualTo(savedStudentDto.id()),
        () -> assertThat(result.firstName()).isEqualTo(savedStudentDto.firstName()),
        () -> assertThat(result.lastName()).isEqualTo(savedStudentDto.lastName()),
        () -> assertThat(result.email()).isEqualTo(savedStudentDto.email()),
        () -> assertThat(result.age()).isEqualTo(savedStudentDto.age()));
  }

  @Test
  void whenListOfStudentsIsSaved_ThenCanBeRecalled() {
    var studentDtoList = TestDataUtil.createListOfTestStudentDto();
    var savedStudentDtoList = studentDtoList.stream().map(studentService::save).toList();
    var page = 0;
    var size = 3;
    var pageable = PageRequest.of(page, size);

    var result = studentService.findAll(pageable);

    assertAll(
        () -> assertThat(result.getContent()).hasSize(size),
        () -> assertThat(result.getNumber()).isEqualTo(page),
        () -> assertThat(result.getSize()).isEqualTo(size),
        () -> assertThat(result.getTotalElements()).isEqualTo(savedStudentDtoList.size()),
        () ->
            assertThat(result.getContent().stream().map(StudentDto::id).toList())
                .containsExactlyInAnyOrderElementsOf(
                    savedStudentDtoList.stream().map(StudentDto::id).toList()));
  }

  @Test
  void whenFindById_ThenReturnStudent() {
    var studentDto = TestDataUtil.createSingleTestStudentDto();
    var savedStudentDto = studentService.save(studentDto);

    var foundStudentDto = studentService.findById(savedStudentDto.id());

    assertThat(foundStudentDto).isEqualTo(savedStudentDto);
  }

  @Test
  void whenCheckExistence_ThenConfirm() {
    var studentDto = TestDataUtil.createSingleTestStudentDto();
    var savedStudentDto = studentService.save(studentDto);

    boolean exists = studentService.exists(savedStudentDto.id());

    assertTrue(exists);
  }

  @Test
  void whenPartialUpdate_ThenUpdateFields() {
    var studentDto = TestDataUtil.createSingleTestStudentDto();
    var savedStudentDto = studentService.save(studentDto);
    var updatedStudentDtoInfo =
        new StudentDto(
            savedStudentDto.id(),
            "UPDATED NAME",
            savedStudentDto.lastName(),
            savedStudentDto.email(),
            savedStudentDto.age(),
            savedStudentDto.studentIdCardDto(),
            savedStudentDto.bookDtoList());

    var updatedStudentDto =
        studentService.partialUpdate(savedStudentDto.id(), updatedStudentDtoInfo);

    assertThat(updatedStudentDto.firstName()).isEqualTo("UPDATED NAME");
  }

  @Test
  void whenDelete_ThenStudentIsNotPresent() {
    var studentDto = TestDataUtil.createSingleTestStudentDto();
    var savedStudentDto = studentService.save(studentDto);

    studentService.delete(savedStudentDto.id());

    assertThrows(
        StudentNotFoundException.class, () -> studentService.findById(savedStudentDto.id()));
  }
}
