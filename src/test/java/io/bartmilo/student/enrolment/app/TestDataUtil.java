package io.bartmilo.student.enrolment.app;

import io.bartmilo.student.enrolment.app.domain.book.model.BookDto;
import io.bartmilo.student.enrolment.app.domain.book.model.BookEntity;
import io.bartmilo.student.enrolment.app.domain.rental.model.RentalEntity;
import io.bartmilo.student.enrolment.app.domain.student.model.IdCardStatus;
import io.bartmilo.student.enrolment.app.domain.student.model.StudentDto;
import io.bartmilo.student.enrolment.app.domain.student.model.StudentEntity;
import io.bartmilo.student.enrolment.app.domain.student.model.StudentIdCardDto;
import io.bartmilo.student.enrolment.app.domain.student.model.StudentIdCardEntity;
import java.time.LocalDateTime;
import java.util.List;

public class TestDataUtil {
  public TestDataUtil() {}

  public static StudentEntity createSingleTestStudentEntity() {
    return StudentEntity.builder()
        .firstName("Carol")
        .lastName("Raccoon")
        .email("carol.raccoon@gmail.com")
        .age(26)
        .build();
  }

  public static StudentDto createSingleTestStudentDto() {
    var studentIdCardDto = createSingleTestStudentIdCardDto(1L);
    return StudentDto.builder()
        .firstName("Carol")
        .lastName("Raccoon")
        .email("carol.raccoon@gmail.com")
        .age(26)
        .studentIdCardDto(studentIdCardDto)
        .build();
  }

  public static List<StudentEntity> createListOfTestStudentEntity() {
    var firstStudentEntity = createSingleTestStudentEntity();
    var secondStudentEntity =
        StudentEntity.builder()
            .firstName("Michael")
            .lastName("Cyrus")
            .email("michael.cyrus@gmail.com")
            .age(41)
            .build();
    var thirdStudentEntity =
        StudentEntity.builder()
            .firstName("Matty")
            .lastName("Gromul")
            .email("matty.gromul@gmail.com")
            .age(28)
            .build();

    return List.of(firstStudentEntity, secondStudentEntity, thirdStudentEntity);
  }

  public static List<StudentDto> createListOfTestStudentDto() {
    var firstStudentDto = createSingleTestStudentDto();
    var secondStudentDto =
        StudentDto.builder()
            .firstName("Michael")
            .lastName("Cyrus")
            .email("michael.cyrus@gmail.com")
            .age(41)
            .build();
    var thirdStudentDto =
        StudentDto.builder()
            .firstName("Matty")
            .lastName("Gromul")
            .email("matty.gromul@gmail.com")
            .age(28)
            .build();

    return List.of(firstStudentDto, secondStudentDto, thirdStudentDto);
  }

  public static StudentIdCardEntity createSingleTestStudentIdCardEntity(Long studentId) {
    var studentEntity = createSingleTestStudentEntity();
    return StudentIdCardEntity.builder()
        .cardNumber("CARD1234567891")
        .status(IdCardStatus.ACTIVE)
        .studentEntity(studentEntity)
        .build();
  }

  public static StudentIdCardDto createSingleTestStudentIdCardDto(Long studentId) {
    return StudentIdCardDto.builder()
        .cardNumber("CARD1234567891")
        .status(IdCardStatus.ACTIVE)
        .studentId(studentId)
        .build();
  }

  public static List<StudentIdCardEntity> createListOfTestStudentIdCardEntity() {
    return List.of(
        createSingleTestStudentIdCardEntity(1L),
        createSingleTestStudentIdCardEntity(2L),
        createSingleTestStudentIdCardEntity(3L));
  }

  public static List<StudentIdCardDto> createListOfTestStudentIdCardDto() {
    return List.of(
        createSingleTestStudentIdCardDto(1L),
        createSingleTestStudentIdCardDto(2L),
        createSingleTestStudentIdCardDto(3L));
  }

  public static BookEntity createSingleTestBookEntity() {
    return BookEntity.builder()
        .bookAuthor("George Orwell")
        .title("1984")
        .isbn("978-0451524935")
        .createdAt(LocalDateTime.of(1949, 6, 8, 0, 0))
        .stock(10)
        .build();
  }

  public static List<BookEntity> createListOfTestBookEntity() {
    return List.of(
        createSingleTestBookEntity(),
        BookEntity.builder()
            .bookAuthor("Jane Austen")
            .title("Pride and Prejudice")
            .isbn("978-1503290563")
            .createdAt(LocalDateTime.of(1813, 1, 28, 0, 0))
            .stock(15)
            .build(),
        BookEntity.builder()
            .bookAuthor("Harper Lee")
            .title("To Kill a Mockingbird")
            .isbn("978-0446310789")
            .createdAt(LocalDateTime.of(1960, 7, 11, 0, 0))
            .stock(20)
            .build());
  }

  public static List<BookDto> createListOfTestBookDto() {
    return List.of(
        createSingleTestBookDto(),
        BookDto.builder()
            .bookAuthor("Jane Austen")
            .title("Pride and Prejudice")
            .isbn("978-1503290563")
            .createdAt(LocalDateTime.of(1813, 1, 28, 0, 0))
            .stock(15)
            .build(),
        BookDto.builder()
            .bookAuthor("Harper Lee")
            .title("To Kill a Mockingbird")
            .isbn("978-0446310789")
            .createdAt(LocalDateTime.of(1960, 7, 11, 0, 0))
            .stock(20)
            .build());
  }

  public static BookDto createSingleTestBookDto() {
    return BookDto.builder()
        .bookAuthor("George Orwell")
        .title("1984")
        .isbn("978-0451524935")
        .createdAt(LocalDateTime.of(1949, 6, 8, 0, 0))
        .stock(10)
        .build();
  }

  public static RentalEntity createSingleTestRentalEntity() {
    var bookEntity = createSingleTestBookEntity();
    var studentEntity = createSingleTestStudentEntity();
    studentEntity.setId(1L);
    studentEntity.setStudentIdCardEntity(createSingleTestStudentIdCardEntity(1L));
    return RentalEntity.builder()
        .id(1L)
        .rentedAt(LocalDateTime.now().minusDays(5))
        .dueDate(LocalDateTime.now().plusDays(25))
        .returnedAt(null)
        .bookEntity(bookEntity)
        .studentEntity(studentEntity)
        .build();
  }
}
