package io.bartmilo.student.enrolment.app.domain.rental.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.bartmilo.student.enrolment.app.TestDataUtil;
import io.bartmilo.student.enrolment.app.domain.book.service.BookService;
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
    var student = TestDataUtil.createSingleTestStudentEntity();
    student = studentService.save(student);
    var book = TestDataUtil.createSingleTestBookEntity();
    var savedBook = bookService.save(book);
    var dueDate = LocalDateTime.now().plusDays(30);

    var rental = rentalService.rentBook(book.getId(), student.getId(), dueDate);
    savedBook.setStock(9);

    assertThat(rental).isNotNull();
    assertThat(rental.getStudentEntity()).isEqualTo(student);
    assertThat(rental.getBookEntity()).isEqualTo(savedBook);
    assertThat(savedBook.getStock()).isEqualTo(9);

    // Return the book
    rentalService.returnBook(rental.getId());

    // Reload book from database to see updated stock
    book =
        bookService
            .findById(book.getId())
            .orElseThrow(() -> new RuntimeException("Book not found"));
    assertThat(book.getStock()).isEqualTo(10); // Stock should be restored

    Long rentalId = rental.getId();
    assertThrows(IllegalStateException.class, () -> rentalService.returnBook(rentalId));
  }

  @Test
  void whenRentBook_BookStockDecreases() {
    var student = TestDataUtil.createSingleTestStudentEntity();
    var savedStudent = studentService.save(student);
    var book = TestDataUtil.createSingleTestBookEntity();
    var savedBook = bookService.save(book);
    var dueDate = LocalDateTime.now().plusDays(30);

    var rental = rentalService.rentBook(savedBook.getId(), savedStudent.getId(), dueDate);

    assertThat(rental).isNotNull();
    assertThat(rental.getStudentEntity()).isEqualTo(savedStudent);
    assertThat(savedBook.getStock()).isEqualTo(10);
    assertThat(rental.getBookEntity().getStock()).isEqualTo(9);
  }

  @Test
  void whenReturnBook_BookStockRestoredAndCannotReReturn() {
    var student = TestDataUtil.createSingleTestStudentEntity();
    student = studentService.save(student);
    var book = TestDataUtil.createSingleTestBookEntity();
    book = bookService.save(book);
    var dueDate = LocalDateTime.now().plusDays(30);
    var rental = rentalService.rentBook(book.getId(), student.getId(), dueDate);

    rentalService.returnBook(rental.getId());

    book =
        bookService
            .findById(book.getId())
            .orElseThrow(() -> new RuntimeException("Book not found"));
    assertThat(book.getStock()).isEqualTo(10); // Stock should be restored to initial value

    Long rentalId = rental.getId();
    assertThrows(IllegalStateException.class, () -> rentalService.returnBook(rentalId));
  }
}
