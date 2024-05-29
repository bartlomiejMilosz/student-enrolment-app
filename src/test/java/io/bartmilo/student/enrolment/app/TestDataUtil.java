package io.bartmilo.student.enrolment.app;

import io.bartmilo.student.enrolment.app.domain.book.model.BookDto;
import io.bartmilo.student.enrolment.app.domain.book.model.BookEntity;
import io.bartmilo.student.enrolment.app.domain.student.model.StudentEntity;
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

  public static List<StudentEntity> createListOfTestStudentEntity() {
    List<StudentEntity> listOfAuthorEntities;
    var firstAuthorEntity = createSingleTestStudentEntity();

    var secondAuthorEntity =
        StudentEntity.builder()
            .firstName("Michael")
            .lastName("Cyrus")
            .email("michael.cyrus@gmail.com")
            .age(41)
            .build();

    var thirdAuthorEntity =
        StudentEntity.builder()
            .firstName("Matty")
            .lastName("Gromul")
            .email("matty.gromul@gmail.com")
            .age(28)
            .build();

    listOfAuthorEntities = List.of(firstAuthorEntity, secondAuthorEntity, thirdAuthorEntity);
    return listOfAuthorEntities;
  }

  public static BookEntity createSingleTestBookEntity() {
    return BookEntity.builder()
        .bookAuthor("George Orwell")
        .title("1984")
        .isbn("978-0451524935")
        .createdAt(LocalDateTime.of(1949, 6, 8, 0, 0)) // Approximate publication date
        .stock(10)
        .build();
  }

  public static List<BookEntity> createListOfTestBookEntity() {
    var nineteenEightyFour = createSingleTestBookEntity();

    var prideAndPrejudice =
        BookEntity.builder()
            .bookAuthor("Jane Austen")
            .title("Pride and Prejudice")
            .isbn("978-1503290563")
            .createdAt(LocalDateTime.of(1813, 1, 28, 0, 0)) // Approximate publication date
            .stock(15)
            .build();

    var toKillAMockingbird =
        BookEntity.builder()
            .bookAuthor("Harper Lee")
            .title("To Kill a Mockingbird")
            .isbn("978-0446310789")
            .createdAt(LocalDateTime.of(1960, 7, 11, 0, 0)) // Approximate publication date
            .stock(20)
            .build();

    return List.of(nineteenEightyFour, prideAndPrejudice, toKillAMockingbird);
  }

  public static BookDto createSingleTestBookDto() {
    return BookDto.builder()
        .bookAuthor("George Orwell")
        .title("1984")
        .isbn("978-0451524935")
        .createdAt(LocalDateTime.of(1949, 6, 8, 0, 0)) // Approximate publication date
        .stock(10)
        .build();
  }

  public static List<BookDto> createListOfTestBookDto() {
    var nineteenEightyFour = createSingleTestBookDto();

    var prideAndPrejudice =
        BookDto.builder()
            .bookAuthor("Jane Austen")
            .title("Pride and Prejudice")
            .isbn("978-1503290563")
            .createdAt(LocalDateTime.of(1813, 1, 28, 0, 0)) // Approximate publication date
            .stock(15)
            .build();

    var toKillAMockingbird =
        BookDto.builder()
            .bookAuthor("Harper Lee")
            .title("To Kill a Mockingbird")
            .isbn("978-0446310789")
            .createdAt(LocalDateTime.of(1960, 7, 11, 0, 0)) // Approximate publication date
            .stock(20)
            .build();

    return List.of(nineteenEightyFour, prideAndPrejudice, toKillAMockingbird);
  }
}
