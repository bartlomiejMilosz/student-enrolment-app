package io.bartmilo.student.enrolment.app.service;

import io.bartmilo.student.enrolment.app.TestDataUtil;
import io.bartmilo.student.enrolment.app.domain.book.service.BookService;
import io.bartmilo.student.enrolment.app.domain.rental.model.RentalEntity;
import io.bartmilo.student.enrolment.app.domain.student.model.StudentIdCardEntity;
import io.bartmilo.student.enrolment.app.domain.rental.repository.RentalRepository;
import io.bartmilo.student.enrolment.app.domain.rental.service.RentalServiceImpl;
import io.bartmilo.student.enrolment.app.domain.student.service.StudentService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class RentalServiceUnitTests {

    @MockBean
    private BookService bookService;
    @MockBean
    private StudentService studentService;
    @MockBean
    private RentalRepository rentalRepository;

    @Autowired
    private RentalServiceImpl rentalService;

    @Test
    void whenRentBook_ThenCreateRentalRecord() {
        // Given
        Long bookId = 1L;
        Long studentId = 1L;
        var dueDate = LocalDateTime.now().plusDays(30);

        var student = TestDataUtil.createSingleTestStudentEntity();
        student.setStudentIdCardEntity(StudentIdCardEntity.builder().build());
        student.getStudentIdCardEntity().setStatus(CardStatus.ACTIVE);
        student.setId(studentId);

        var book = TestDataUtil.createSingleTestBookEntity();
        book.setId(bookId);
        book.setStock(5);

        when(studentService.save(student)).thenReturn(student);
        when(bookService.save(book)).thenReturn(book);
        when(studentService.findById(studentId)).thenReturn(Optional.of(student));
        when(bookService.findById(bookId)).thenReturn(Optional.of(book));
        when(rentalRepository.save(any(RentalEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        var rental = rentalService.rentBook(bookId, studentId, dueDate);

        // Then
        verify(bookService).save(book);
        assertAll(
                () -> assertThat(rental.getStudentEntity()).isEqualTo(student),
                () -> assertThat(rental.getBookEntity()).isEqualTo(book),
                () -> assertThat(rental.getDueDate()).isEqualTo(dueDate),
                () -> assertThat(book.getStock()).isEqualTo(4),  // Check if stock is decremented
                () -> assertThat(rental.getRentedAt()).isNotNull()  // Ensure rentedAt is set correctly
        );
    }

    @Test
    void whenReturnBook_ThenUpdateStockAndMarkReturned() {
        // Given
        Long rentalId = 1L;
        var now = LocalDateTime.now();

        var book = TestDataUtil.createSingleTestBookEntity();
        book.setId(1L);
        book.setStock(5);  // Assume stock before return

        var rental = RentalEntity.builder()
                .id(rentalId)
                .bookEntity(book)
                .rentedAt(now.minusDays(10))
                .returnedAt(null)
                .build();

        when(rentalRepository.findById(rentalId)).thenReturn(Optional.of(rental));
        when(bookService.save(book)).thenReturn(book);
        when(rentalRepository.save(rental)).thenReturn(rental);

        // When
        var returnedRental = rentalService.returnBook(rentalId);

        // Then
        verify(bookService).save(book);
        verify(rentalRepository).save(rental);
        assertThat(returnedRental.getReturnedAt()).isNotNull();
        assertThat(book.getStock()).isEqualTo(6);  // Check if stock is incremented
        assertThat(returnedRental.getReturnedAt()).isAfterOrEqualTo(now);  // Check if returnedAt is set
    }

    @Test
    void whenReturnBookAndBookAlreadyReturned_ThenThrowIllegalStateException() {
        // Given
        Long rentalId = 1L;
        LocalDateTime now = LocalDateTime.now();

        var book = TestDataUtil.createSingleTestBookEntity();
        book.setId(1L);
        book.setStock(5);

        var rental = RentalEntity.builder()
                .id(rentalId)
                .bookEntity(book)
                .rentedAt(now.minusDays(10))
                .returnedAt(now.minusDays(5))
                .build();

        when(rentalRepository.findById(rentalId)).thenReturn(Optional.of(rental));

        // Then
        var exception = assertThrows(
                IllegalStateException.class,
                () -> rentalService.returnBook(rentalId)
        );

        assertThat(exception.getMessage()).isEqualTo("This book has already been returned.");
    }

    @Test
    void whenReturnBookAndRentalDoesNotExist_ThenThrowEntityNotFoundException() {
        // Given
        Long rentalId = 99L;  // Non-existent rental ID
        when(rentalRepository.findById(rentalId)).thenReturn(Optional.empty());

        // Then
        var exception = assertThrows(
                EntityNotFoundException.class,
                () -> rentalService.returnBook(rentalId)
        );

        assertThat(exception.getMessage()).isEqualTo("Rental not found with ID: " + rentalId);
    }
}
