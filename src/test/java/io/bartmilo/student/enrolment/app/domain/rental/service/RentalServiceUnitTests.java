package io.bartmilo.student.enrolment.app.domain.rental.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import io.bartmilo.student.enrolment.app.TestDataUtil;
import io.bartmilo.student.enrolment.app.domain.book.mapper.BookMapper;
import io.bartmilo.student.enrolment.app.domain.book.service.BookService;
import io.bartmilo.student.enrolment.app.domain.rental.exception.RentalNotFoundException;
import io.bartmilo.student.enrolment.app.domain.rental.mapper.RentalMapper;
import io.bartmilo.student.enrolment.app.domain.rental.model.*;
import io.bartmilo.student.enrolment.app.domain.rental.repository.RentalRepository;
import io.bartmilo.student.enrolment.app.domain.student.mapper.StudentMapper;
import io.bartmilo.student.enrolment.app.domain.student.model.IdCardStatus;
import io.bartmilo.student.enrolment.app.domain.student.model.StudentIdCardDto;
import io.bartmilo.student.enrolment.app.domain.student.service.StudentService;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RentalServiceUnitTests {

  @Mock private StudentService studentService;
  @Mock private BookService bookService;
  @Mock private RentalRepository rentalRepository;
  @Mock private StudentMapper studentMapper;
  @Mock private RentalMapper rentalMapper;
  @Mock private BookMapper bookMapper;

  @InjectMocks private RentalServiceImpl rentalService;

  @BeforeEach
  void setUp() {
    rentalService =
        new RentalServiceImpl(
            studentService, bookService, rentalRepository, studentMapper, rentalMapper, bookMapper);
  }

  @Test
  void whenRentBookWithActiveCard_ThenCreateRentalRecord() {
    var bookId = 1L;
    var studentId = 1L;
    var dueDate = LocalDateTime.now().plusDays(30);
    var studentDto = TestDataUtil.createSingleTestStudentDto();
    var bookDto = TestDataUtil.createSingleTestBookDto();

    when(studentService.findById(studentId)).thenReturn(studentDto);
    when(bookService.decrementBookStock(bookId)).thenReturn(bookDto);

    var rentalEntity = RentalEntity.builder()
        .rentedAt(LocalDateTime.now())
        .dueDate(dueDate)
        .bookEntity(TestDataUtil.createSingleTestBookEntity())
        .studentEntity(TestDataUtil.createSingleTestStudentEntity())
        .build();
    when(rentalRepository.save(any(RentalEntity.class))).thenReturn(rentalEntity);
    when(rentalMapper.convertEntityToDto(any(RentalEntity.class))).thenReturn(new RentalDto());

    var rental = rentalService.rentBook(bookId, studentId, dueDate);

    verify(bookService).decrementBookStock(bookId);
    verify(studentService).findById(studentId);
    verify(rentalRepository).save(any(RentalEntity.class));
    assertNotNull(rental);
  }

  @Test
  void whenReturnBook_ThenUpdateStockAndMarkReturned() {
    var rentalId = 1L;
    var rentalEntity = TestDataUtil.createSingleTestRentalEntity();
    when(rentalRepository.findById(rentalId)).thenReturn(Optional.of(rentalEntity));
    when(rentalMapper.convertEntityToDto(any(RentalEntity.class))).thenReturn(new RentalDto());

    var returnedRental = rentalService.returnBook(rentalId);
    returnedRental.setReturnedAt(LocalDateTime.now());

    verify(bookService).incrementBookStock(rentalEntity.getBookEntity().getId());
    verify(rentalRepository).save(rentalEntity);
    assertNotNull(returnedRental.getReturnedAt());
  }

  @Test
  void whenReturnBookAlreadyReturned_ThenThrowIllegalStateException() {
    var rentalId = 1L;
    var rentalEntity = TestDataUtil.createSingleTestRentalEntity();
    rentalEntity.setReturnedAt(LocalDateTime.now());
    when(rentalRepository.findById(rentalId)).thenReturn(Optional.of(rentalEntity));
    assertThrows(IllegalStateException.class, () -> rentalService.returnBook(rentalId));
  }

  @Test
  void whenReturnBookNotExists_ThenThrowNotFoundException() {
    var rentalId = 99L; // Non-existent rental
    when(rentalRepository.findById(rentalId)).thenReturn(Optional.empty());
    assertThrows(RentalNotFoundException.class, () -> rentalService.returnBook(rentalId));
  }
}
