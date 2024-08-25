package io.bartmilo.student.enrolment.app.domain.student.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import io.bartmilo.student.enrolment.app.TestDataUtil;
import io.bartmilo.student.enrolment.app.domain.student.exception.StudentNotFoundException;
import io.bartmilo.student.enrolment.app.domain.student.mapper.StudentIdCardMapper;
import io.bartmilo.student.enrolment.app.domain.student.mapper.StudentMapper;
import io.bartmilo.student.enrolment.app.domain.student.model.StudentDto;
import io.bartmilo.student.enrolment.app.domain.student.model.StudentEntity;
import io.bartmilo.student.enrolment.app.domain.student.model.StudentIdCardDto;
import io.bartmilo.student.enrolment.app.domain.student.model.StudentIdCardEntity;
import io.bartmilo.student.enrolment.app.domain.student.repository.StudentIdCardRepository;
import io.bartmilo.student.enrolment.app.domain.student.repository.StudentRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
class StudentServiceUnitTests {

  @Mock private StudentRepository studentRepositoryMock;
  @Mock private StudentIdCardRepository studentIdCardRepositoryMock;
  @Mock private StudentIdCardService studentIdCardServiceMock;
  @Mock private StudentMapper studentMapperMock;
  @Mock private StudentIdCardMapper studentIdCardMapperMock;

  private StudentService studentService;

  @BeforeEach
  void setUp() {
    this.studentService =
        new StudentServiceImpl(
            studentRepositoryMock,
            studentIdCardServiceMock,
            studentMapperMock,
            studentIdCardMapperMock,
            studentIdCardRepositoryMock);
  }

  @Test
  void whenStudentIsSaved_ThenCanBeRecalled() {
    var studentDto = TestDataUtil.createSingleTestStudentDto();
    var studentEntity = TestDataUtil.createSingleTestStudentEntity();
    var studentIdCardDto = TestDataUtil.createSingleTestStudentIdCardDto(1L);
    var studentIdCardEntity = TestDataUtil.createSingleTestStudentIdCardEntity(1L);
    when(studentMapperMock.convertDtoToEntity(any(StudentDto.class))).thenReturn(studentEntity);
    when(studentRepositoryMock.save(any(StudentEntity.class))).thenReturn(studentEntity);
    when(studentMapperMock.convertEntityToDto(any(StudentEntity.class))).thenReturn(studentDto);
    when(studentIdCardServiceMock.generateStudentIdCard(any(StudentDto.class)))
        .thenReturn(studentIdCardDto);
    when(studentIdCardMapperMock.convertDtoToEntity(any(StudentIdCardDto.class)))
        .thenReturn(studentIdCardEntity);
    when(studentIdCardRepositoryMock.save(any(StudentIdCardEntity.class)))
        .thenReturn(studentIdCardEntity);
    var savedStudentDto = studentService.save(studentDto);

    verify(studentRepositoryMock, times(2)).save(studentEntity);
    verify(studentIdCardRepositoryMock).save(studentIdCardEntity);
    verify(studentIdCardServiceMock).generateStudentIdCard(savedStudentDto);
    var studentEntityCaptor = ArgumentCaptor.forClass(StudentEntity.class);
    verify(studentRepositoryMock, times(2)).save(studentEntityCaptor.capture());
    var capturedStudentEntity = studentEntityCaptor.getValue();
    assertAll(
        () ->
            assertNotNull(
                capturedStudentEntity.getStudentIdCardEntity(), "Student ID Card should be linked"),
        () ->
            assertEquals(
                studentDto,
                savedStudentDto,
                "The saved and returned student DTO should be the same"));
  }

  @Test
  void whenListOfStudentsIsSaved_ThenAllStudentsShouldBeCorrectlySavedAndReturned() {
    var studentEntityList = TestDataUtil.createListOfTestStudentEntity();
    var studentDtoList = TestDataUtil.createListOfTestStudentDto();
    var studentIdCardEntityList = TestDataUtil.createListOfTestStudentIdCardEntity();
    var studentIdCardDtoList = TestDataUtil.createListOfTestStudentIdCardDto();
    for (int i = 0; i < studentEntityList.size(); i++) {
      var studentEntity = studentEntityList.get(i);
      var studentDto = studentDtoList.get(i);
      var studentIdCardDto = studentIdCardDtoList.get(i);
      var studentIdCardEntity = studentIdCardEntityList.get(i);

      when(studentMapperMock.convertDtoToEntity(studentDto)).thenReturn(studentEntity);
      when(studentRepositoryMock.save(studentEntity)).thenReturn(studentEntity);
      when(studentMapperMock.convertEntityToDto(studentEntity)).thenReturn(studentDto);
      when(studentIdCardServiceMock.generateStudentIdCard(studentDto)).thenReturn(studentIdCardDto);
      when(studentIdCardMapperMock.convertDtoToEntity(studentIdCardDto))
          .thenReturn(studentIdCardEntity);
      when(studentIdCardRepositoryMock.save(studentIdCardEntity)).thenReturn(studentIdCardEntity);
    }

    var savedStudentDtos = studentDtoList.stream().map(studentService::save).toList();

    for (int i = 0; i < studentEntityList.size(); i++) {
      var expectedSavedEntity = studentEntityList.get(i);
      var expectedSavedIdCardEntity = studentIdCardEntityList.get(i);

      verify(studentRepositoryMock, times(2)).save(expectedSavedEntity);
      verify(studentIdCardRepositoryMock, times(3)).save(expectedSavedIdCardEntity);
      verify(studentIdCardServiceMock).generateStudentIdCard(savedStudentDtos.get(i));
      assertThat(savedStudentDtos.get(i)).isEqualToComparingFieldByField(studentDtoList.get(i));
    }
  }

  @Test
  void whenFindAll_ThenReturnPageOfStudents() {
    var pageable = PageRequest.of(0, 10);
    var studentEntityList = TestDataUtil.createListOfTestStudentEntity();
    var studentDtoList = TestDataUtil.createListOfTestStudentDto();
    var expectedPageOfEntities = new PageImpl<>(studentEntityList, pageable, studentEntityList.size());
    when(studentRepositoryMock.findAll(pageable)).thenReturn(expectedPageOfEntities);
    for (int i = 0; i < studentDtoList.size(); i++) {
      var studentEntity = studentEntityList.get(i);
      var studentDto = studentDtoList.get(i);
      when(studentMapperMock.convertEntityToDto(studentEntity)).thenReturn(studentDto);
    }

    var result = studentService.findAll(pageable);

    assertAll(
        () -> assertThat(result.getContent()).isEqualTo(studentDtoList),
        () -> assertThat(result.getTotalElements()).isEqualTo(studentDtoList.size()));
  }

  @Test
  void whenFindById_ThenReturnStudent() {
    var studentId = 1L;
    var student = TestDataUtil.createSingleTestStudentEntity();
    student.setId(studentId);
    var expectedDto = TestDataUtil.createSingleTestStudentDto();
    when(studentRepositoryMock.findById(studentId)).thenReturn(Optional.of(student));
    when(studentMapperMock.convertEntityToDto(student)).thenReturn(expectedDto);

    var result = studentService.findById(studentId);

    assertThat(result).isEqualToComparingFieldByField(expectedDto);
  }

  @Test
  void whenCheckIfStudentExists_ThenReturnTrueOrFalse() {
    var studentId = 1L;
    when(studentRepositoryMock.existsById(studentId)).thenReturn(true);

    boolean exists = studentService.exists(studentId);

    assertThat(exists).isTrue();
  }

  @Test
  void whenPartialUpdate_ThenStudentIsUpdated() {
    var studentId = 1L;
    var student = TestDataUtil.createSingleTestStudentEntity();
    student.setId(studentId);
    var partialUpdateDto =
        new StudentDto(
            studentId,
            "UPDATED",
            student.getLastName(),
            student.getEmail(),
            student.getAge(),
            null,
            null);
    var updatedStudentEntity =
        new StudentEntity(
            student.getId(),
            partialUpdateDto.firstName(),
            student.getLastName(),
            student.getEmail(),
            student.getAge(),
            null,
            null);

    when(studentRepositoryMock.findById(studentId)).thenReturn(Optional.of(student));
    when(studentRepositoryMock.save(any(StudentEntity.class))).thenReturn(updatedStudentEntity);
    when(studentMapperMock.convertEntityToDto(updatedStudentEntity)).thenReturn(partialUpdateDto);

    var updatedStudentDto = studentService.partialUpdate(studentId, partialUpdateDto);

    assertAll(
        () -> verify(studentRepositoryMock).save(updatedStudentEntity),
        () -> assertEquals(partialUpdateDto.firstName(), updatedStudentDto.firstName()));
  }

  @Test
  void whenDelete_ThenRepositoryDeleteIsCalled() {
    var studentId = 1L;
    doNothing().when(studentRepositoryMock).deleteById(studentId);

    studentService.delete(studentId);

    verify(studentRepositoryMock).deleteById(studentId);
    assertThrows(StudentNotFoundException.class, () -> studentService.findById(studentId));
  }
}
