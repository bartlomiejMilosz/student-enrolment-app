/*
package io.bartmilo.student.enrolment.app.domain.rental.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import io.bartmilo.student.enrolment.app.TestDataUtil;
import io.bartmilo.student.enrolment.app.domain.book.service.BookService;
import io.bartmilo.student.enrolment.app.domain.rental.model.RentalEntity;
import io.bartmilo.student.enrolment.app.domain.rental.repository.RentalRepository;
import io.bartmilo.student.enrolment.app.domain.student.model.StudentIdCardEntity;
import io.bartmilo.student.enrolment.app.domain.student.service.StudentService;
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RentalServiceUnitTests {

  @Mock private StudentService studentService;
  @Mock private BookService bookService;
  @Mock private RentalRepository rentalRepository;

  private RentalService rentalService;

  @BeforeEach
  void setUp() {
    this.rentalService = new RentalServiceImpl(studentService, bookService, rentalRepository);
  }

  @Test
  void whenRentBook_ThenCreateRentalRecord() {
    Long bookId = 1L;
    Long studentId = 1L;
    var dueDate = LocalDateTime.now().plusDays(30);

    var student = TestDataUtil.createSingleTestStudentEntity();
    student.setStudentIdCardEntity(StudentIdCardEntity.builder().build());
    student.getStudentIdCardEntity().setStatus(StudentIdCardEntity.CardStatus.ACTIVE);
    student.setId(studentId);

    var book = TestDataUtil.createSingleTestBookEntity();
    book.setId(bookId);
    book.setStock(5);

    when(studentService.findById(studentId)).thenReturn(Optional.of(student));
    when(bookService.findById(bookId)).thenReturn(Optional.of(book));
    when(rentalRepository.save(any(RentalEntity.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    var rental = rentalService.rentBook(bookId, studentId, dueDate);

    verify(bookService).findById(bookId);
    verify(studentService).findById(studentId);
    verify(rentalRepository).save(any(RentalEntity.class));

    assertAll(
        () -> assertThat(rental.getStudentEntity()).isEqualTo(student),
        () -> assertThat(rental.getBookEntity()).isEqualTo(book),
        () -> assertThat(rental.getDueDate()).isEqualTo(dueDate),
        () -> assertThat(book.getStock()).isEqualTo(4), // Check if stock is decremented
        () -> assertThat(rental.getRentedAt()).isNotNull() // Ensure rentedAt is set correctly
        );
  }

  @Test
  void whenReturnBook_ThenUpdateStockAndMarkReturned() {
    Long rentalId = 1L;
    var now = LocalDateTime.now();

    var book = TestDataUtil.createSingleTestBookEntity();
    book.setId(1L);
    book.setStock(5); // Assume stock before return

    var rental =
        RentalEntity.builder()
            .id(rentalId)
            .bookEntity(book)
            .rentedAt(now.minusDays(10))
            .returnedAt(null)
            .build();

    when(rentalRepository.findById(rentalId)).thenReturn(Optional.of(rental));
    when(bookService.save(book)).thenReturn(book);
    when(rentalRepository.save(rental)).thenReturn(rental);

    var returnedRental = rentalService.returnBook(rentalId);

    verify(bookService).save(book);
    verify(rentalRepository).save(rental);
    assertThat(returnedRental.getReturnedAt()).isNotNull();
    assertThat(book.getStock()).isEqualTo(6); // Check if stock is incremented
    assertThat(returnedRental.getReturnedAt()).isAfterOrEqualTo(now); // Check if returnedAt is set
  }

  @Test
  void whenReturnBookAndBookAlreadyReturned_ThenThrowIllegalStateException() {
    Long rentalId = 1L;
    LocalDateTime now = LocalDateTime.now();

    var book = TestDataUtil.createSingleTestBookEntity();
    book.setId(1L);
    book.setStock(5);

    var rental =
        RentalEntity.builder()
            .id(rentalId)
            .bookEntity(book)
            .rentedAt(now.minusDays(10))
            .returnedAt(now.minusDays(5))
            .build();

    when(rentalRepository.findById(rentalId)).thenReturn(Optional.of(rental));

    var exception =
        assertThrows(IllegalStateException.class, () -> rentalService.returnBook(rentalId));

    assertThat(exception.getMessage()).isEqualTo("This book has already been returned.");
  }

  @Test
  void whenReturnBookAndRentalDoesNotExist_ThenThrowEntityNotFoundException() {
    Long rentalId = 99L; // Non-existent rental ID
    when(rentalRepository.findById(rentalId)).thenReturn(Optional.empty());

    var exception =
        assertThrows(EntityNotFoundException.class, () -> rentalService.returnBook(rentalId));

    assertThat(exception.getMessage()).isEqualTo("Rental not found with ID: " + rentalId);
  }
}
*/
