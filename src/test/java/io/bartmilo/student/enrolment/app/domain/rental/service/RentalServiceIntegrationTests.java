package io.bartmilo.student.enrolment.app.domain.rental.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.bartmilo.student.enrolment.app.TestDataUtil;
import io.bartmilo.student.enrolment.app.domain.book.model.BookDto;
import io.bartmilo.student.enrolment.app.domain.book.service.BookService;
import io.bartmilo.student.enrolment.app.domain.rental.exception.RentalNotFoundException;
import io.bartmilo.student.enrolment.app.domain.rental.model.RentalDto;
import io.bartmilo.student.enrolment.app.domain.student.model.IdCardStatus;
import io.bartmilo.student.enrolment.app.domain.student.service.StudentService;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class RentalServiceIntegrationTests {

  @Autowired private RentalService rentalService;

  @Autowired private BookService bookService;

  @Autowired private StudentService studentService;

  @Test
  void whenRentAndReturnBook_Integration() {
    var studentDto = TestDataUtil.createSingleTestStudentDto();
    var savedStudentEntity = studentService.save(studentDto);
    var bookDto = TestDataUtil.createSingleTestBookDto();
    var savedBookEntity = bookService.save(bookDto);
    var dueDate = LocalDateTime.now().plusDays(30);

    var rentalDto = rentalService.rentBook(savedBookEntity.getId(), savedStudentEntity.id(), dueDate);
    assertThat(rentalDto).isNotNull();
    assertThat(rentalDto.getStudentId()).isEqualTo(savedStudentEntity.id());
    assertThat(rentalDto.getBookId()).isEqualTo(savedBookEntity.getId());

    var updatedBookEntity = bookService.findById(savedBookEntity.getId());
    assertThat(updatedBookEntity.getStock()).isEqualTo(savedBookEntity.getStock() - 1);

    rentalService.returnBook(rentalDto.getId());

    updatedBookEntity = bookService.findById(savedBookEntity.getId());
    assertThat(updatedBookEntity.getStock()).isEqualTo(savedBookEntity.getStock());

    var rentalId = rentalDto.getId();
    assertThrows(IllegalStateException.class, () -> rentalService.returnBook(rentalId));
  }

  @Test
  void whenRentBook_BookStockDecreases() {
    var studentDto = TestDataUtil.createSingleTestStudentDto();
    var savedStudentEntity = studentService.save(studentDto);
    var bookDto = TestDataUtil.createSingleTestBookDto();
    var savedBookEntity = bookService.save(bookDto);
    var dueDate = LocalDateTime.now().plusDays(30);

    var rentalDto = rentalService.rentBook(savedBookEntity.getId(), savedStudentEntity.id(), dueDate);

    assertThat(rentalDto).isNotNull();
    assertThat(rentalDto.getStudentId()).isEqualTo(savedStudentEntity.id());
    assertThat(rentalDto.getBookId()).isEqualTo(savedBookEntity.getId());

    var updatedBookEntity = bookService.findById(savedBookEntity.getId());
    assertThat(updatedBookEntity.getStock()).isEqualTo(savedBookEntity.getStock() - 1);
  }

  @Test
  void whenReturnBook_BookStockRestoredAndCannotReReturn() {
    var studentDto = TestDataUtil.createSingleTestStudentDto();
    var savedStudentEntity = studentService.save(studentDto);
    var bookDto = TestDataUtil.createSingleTestBookDto();
    var savedBookEntity = bookService.save(bookDto);
    var dueDate = LocalDateTime.now().plusDays(30);
    var rentalDto = rentalService.rentBook(savedBookEntity.getId(), savedStudentEntity.id(), dueDate);

    rentalService.returnBook(rentalDto.getId());

    var updatedBookEntity = bookService.findById(savedBookEntity.getId());
    assertThat(updatedBookEntity.getStock()).isEqualTo(savedBookEntity.getStock());

    var rentalId = rentalDto.getId();
    assertThrows(IllegalStateException.class, () -> rentalService.returnBook(rentalId));
  }
}
