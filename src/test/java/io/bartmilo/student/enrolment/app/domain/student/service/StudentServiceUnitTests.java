package io.bartmilo.student.enrolment.app.domain.student.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.*;

import io.bartmilo.student.enrolment.app.TestDataUtil;
import io.bartmilo.student.enrolment.app.domain.student.model.StudentEntity;
import io.bartmilo.student.enrolment.app.domain.student.model.StudentIdCardEntity;
import io.bartmilo.student.enrolment.app.domain.student.repository.StudentRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
class StudentServiceUnitTests {

  @Mock private StudentRepository studentRepositoryMock;

  @Mock private StudentIdCardService studentIdCardServiceMock;

  private StudentService studentService;

  @BeforeEach
  void setUp() {
    this.studentService = new StudentServiceImpl(studentRepositoryMock, studentIdCardServiceMock);
  }

  @Test
  void whenStudentIsSaved_ThanCanBeRecalled() {
    var studentEntity = TestDataUtil.createSingleTestStudentEntity();
    var idCard = new StudentIdCardEntity(); // Create a test ID card
    when(studentIdCardServiceMock.generateStudentIdCard(studentEntity)).thenReturn(idCard);
    when(studentRepositoryMock.save(studentEntity)).thenReturn(studentEntity);

    var savedStudent = studentService.save(studentEntity);

    verify(studentRepositoryMock, times(1)).save(studentEntity);

    assertAll(
        () -> assertThat(savedStudent.getStudentIdCardEntity()).isEqualTo(idCard),
        () -> assertThat(savedStudent).isEqualTo(studentEntity));
  }

  @Test
  void whenListOfStudentsIsSaved_ThanCanBeRecalled() {
    var studentEntityList = TestDataUtil.createListOfTestStudentEntities();
    for (StudentEntity student : studentEntityList) {
      when(studentRepositoryMock.save(student)).thenReturn(student);
    }

    studentEntityList.forEach(studentService::save);

    for (StudentEntity student : studentEntityList) {
      verify(studentRepositoryMock).save(student);
      assertThat(studentService.save(student)).isEqualTo(student);
    }
  }

  @Test
  void whenFindAll_ThenReturnPageOfStudents() {
    var pageable = PageRequest.of(0, 10);
    var students = TestDataUtil.createListOfTestStudentEntities();
    var expectedPage = new PageImpl<>(students, pageable, students.size());
    when(studentRepositoryMock.findAll(pageable)).thenReturn(expectedPage);

    var result = studentService.findAll(pageable);

    assertAll(
        () -> assertThat(result.getContent()).isEqualTo(students),
        () -> assertThat(result.getTotalElements()).isEqualTo(students.size()));
  }

  @Test
  void whenFindById_ThenReturnStudent() {
    var student = TestDataUtil.createSingleTestStudentEntity();
    when(studentRepositoryMock.findById(student.getId())).thenReturn(Optional.of(student));

    var result = studentService.findById(student.getId());

    assertThat(result).isPresent().contains(student);
  }

  @Test
  void whenCheckIfStudentExists_ThenReturnTrueOrFalse() {
    Long studentId = 1L;
    when(studentRepositoryMock.existsById(studentId)).thenReturn(true);

    boolean exists = !studentService.isExists(studentId);

    assertThat(exists).isTrue();
  }

  @Test
  void whenPartialUpdate_ThenStudentIsUpdated() {
    Long studentId = 1L;
    var student = TestDataUtil.createSingleTestStudentEntity();
    student.setId(studentId);
    var updateInfo = new StudentEntity();
    updateInfo.setFirstName("UpdatedFirstName");

    when(studentRepositoryMock.findById(studentId)).thenReturn(Optional.of(student));
    when(studentRepositoryMock.save(any(StudentEntity.class))).thenReturn(student);

    var updatedStudent = studentService.partialUpdate(studentId, updateInfo);

    verify(studentRepositoryMock).save(student); // Verify save was called on the original student
    assertThat(updatedStudent.getFirstName()).isEqualTo(updateInfo.getFirstName());
  }

  @Test
  void whenDelete_ThenRepositoryDeleteIsCalled() {
    Long studentId = 1L;
    doNothing().when(studentRepositoryMock).deleteById(studentId);

    studentService.delete(studentId);

    verify(studentRepositoryMock, times(1)).deleteById(studentId);
    assertThat(studentService.findById(studentId)).isEmpty();
  }
}
