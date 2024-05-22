package io.bartmilo.student.enrolment.app.domain.rental.service;

import io.bartmilo.student.enrolment.app.domain.book.model.BookDto;
import io.bartmilo.student.enrolment.app.domain.book.model.BookEntity;
import io.bartmilo.student.enrolment.app.domain.book.service.BookService;
import io.bartmilo.student.enrolment.app.domain.rental.model.RentalDto;
import io.bartmilo.student.enrolment.app.domain.rental.model.RentalEntity;
import io.bartmilo.student.enrolment.app.domain.rental.repository.RentalRepository;
import io.bartmilo.student.enrolment.app.domain.student.model.IdCardStatus;
import io.bartmilo.student.enrolment.app.domain.student.model.StudentEntity;
import io.bartmilo.student.enrolment.app.domain.student.service.StudentService;
import io.bartmilo.student.enrolment.app.util.DomainMapper;
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RentalServiceImpl implements RentalService {
  private static final Logger LOGGER = LoggerFactory.getLogger(RentalServiceImpl.class);

  private final StudentService studentService;
  private final BookService bookService;
  private final RentalRepository rentalRepository;
  private final DomainMapper domainMapper;

  public RentalServiceImpl(
      StudentService studentService,
      BookService bookService,
      RentalRepository rentalRepository,
      DomainMapper domainMapper) {
    this.studentService = studentService;
    this.bookService = bookService;
    this.rentalRepository = rentalRepository;
    this.domainMapper = domainMapper;
  }

  @Override
  @Transactional
  public RentalDto rentBook(Long bookId, Long studentId, LocalDateTime dueDate) {
    LOGGER.info(
        "Attempting to rent book with ID: {} for student with ID: {} due by {}",
        bookId,
        studentId,
        dueDate);
    var studentDto = studentService.findById(studentId);
    LOGGER.info("Student details retrieved: {}", studentDto);

    // Check the status of the student's ID card
    if (studentDto.studentIdCardDto() == null
        || studentDto.studentIdCardDto().status() != IdCardStatus.ACTIVE) {
      LOGGER.warn("Attempt to rent with inactive ID card by student ID: {}", studentId);
      throw new IllegalStateException("Student's ID card is not active.");
    }

    var bookDto = bookService.findById(bookId);
    LOGGER.info("Book details retrieved: {}", bookDto);

    if (bookDto.stock() == null || bookDto.stock() <= 0) {
      LOGGER.error("Book stock unavailable for book ID: {}", bookId);
      throw new IllegalArgumentException("Book is not available for rent.");
    }

    var rentedBookDto =
        new BookDto(
            bookDto.id(),
            bookDto.bookAuthor(),
            bookDto.title(),
            bookDto.isbn(),
            bookDto.createdAt(),
            bookDto.stock() - 1);
    bookService.save(rentedBookDto); // Save the updated book stock
    LOGGER.info("Updated book stock for book ID: {}. New stock: {}", bookId, rentedBookDto.stock());

    var rentedBookEntity = domainMapper.convertDtoToEntity(rentedBookDto, BookEntity.class);
    var studentEntity = domainMapper.convertDtoToEntity(studentDto, StudentEntity.class);

    var rentalEntity = new RentalEntity();
    rentalEntity.setRentedAt(LocalDateTime.now());
    rentalEntity.setBookEntity(rentedBookEntity);
    rentalEntity.setStudentEntity(studentEntity);
    rentalEntity.setDueDate(dueDate);
    LOGGER.info("Created rental record: {}", rentalEntity);

    LOGGER.info("Saving rental to the database.");

    var savedRentalEntity = rentalRepository.save(rentalEntity);
    return domainMapper.convertEntityToDto(savedRentalEntity, RentalDto.class);
  }

  @Override
  @Transactional
  public RentalDto returnBook(Long rentalId) {
    var rentalEntity =
        rentalRepository
            .findById(rentalId)
            .orElseThrow(
                () -> new EntityNotFoundException("Rental not found with ID: " + rentalId));

    if (rentalEntity.getReturnedAt() != null) {
      throw new IllegalStateException("This book has already been returned.");
    }

    LOGGER.info(String.valueOf(rentalEntity.getBookEntity().getStock()));
    var bookEntity = rentalEntity.getBookEntity();
    var returnedBookDto =
        new BookDto(
            bookEntity.getId(),
            bookEntity.getBookAuthor(),
            bookEntity.getTitle(),
            bookEntity.getIsbn(),
            bookEntity.getCreatedAt(),
            bookEntity.getStock() + 1);

    bookService.save(returnedBookDto);
    LOGGER.info(String.valueOf(returnedBookDto.stock()));

    rentalEntity.setReturnedAt(LocalDateTime.now());
    rentalRepository.save(rentalEntity);

    LOGGER.info("Book returned successfully: {}", rentalEntity);
    return domainMapper.convertEntityToDto(rentalEntity, RentalDto.class);
  }
}
