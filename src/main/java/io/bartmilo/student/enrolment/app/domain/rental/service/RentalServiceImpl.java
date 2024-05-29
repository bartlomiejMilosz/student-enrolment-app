package io.bartmilo.student.enrolment.app.domain.rental.service;

import io.bartmilo.student.enrolment.app.domain.book.mapper.BookMapper;
import io.bartmilo.student.enrolment.app.domain.book.service.BookService;
import io.bartmilo.student.enrolment.app.domain.rental.exception.RentalNotFoundException;
import io.bartmilo.student.enrolment.app.domain.rental.mapper.RentalMapper;
import io.bartmilo.student.enrolment.app.domain.rental.model.RentalDto;
import io.bartmilo.student.enrolment.app.domain.rental.model.RentalEntity;
import io.bartmilo.student.enrolment.app.domain.rental.repository.RentalRepository;
import io.bartmilo.student.enrolment.app.domain.student.mapper.StudentMapper;
import io.bartmilo.student.enrolment.app.domain.student.model.IdCardStatus;
import io.bartmilo.student.enrolment.app.domain.student.service.StudentService;
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
  private final StudentMapper studentMapper;
  private final BookMapper bookMapper;
  private final RentalMapper rentalMapper;

  public RentalServiceImpl(
      StudentService studentService,
      BookService bookService,
      RentalRepository rentalRepository,
      StudentMapper studentMapper,
      RentalMapper rentalMapper,
      BookMapper bookMapper) {
    this.studentService = studentService;
    this.bookService = bookService;
    this.rentalRepository = rentalRepository;
    this.studentMapper = studentMapper;
    this.bookMapper = bookMapper;
    this.rentalMapper = rentalMapper;
  }

  @Override
  @Transactional
  public RentalDto rentBook(Long bookId, Long studentId, LocalDateTime dueDate) {
    LOGGER.info(
        "Attempting to rent book with ID: {} for student with ID: {} due by {}",
        bookId,
        studentId,
        dueDate);
    var bookDto = bookService.decrementBookStock(bookId);
    var studentDto = studentService.findById(studentId);

    if (studentDto.studentIdCardDto().status() != IdCardStatus.ACTIVE) {
      throw new IllegalStateException("Student's ID card is not active.");
    }

    var rentalEntity =
        RentalEntity.builder()
            .rentedAt(LocalDateTime.now())
            .dueDate(dueDate)
            .bookEntity(bookMapper.convertDtoToEntity(bookDto))
            .studentEntity(studentMapper.convertDtoToEntity(studentDto))
            .build();
    rentalRepository.save(rentalEntity);
    LOGGER.info("Rental saved: {}", rentalEntity);
    return rentalMapper.convertEntityToDto(rentalEntity);
  }

  @Override
  @Transactional
  public RentalDto returnBook(Long rentalId) {
    var rentalEntity =
        rentalRepository
            .findById(rentalId)
            .orElseThrow(
                () -> new RentalNotFoundException("Rental not found with ID: " + rentalId));

    if (rentalEntity.getReturnedAt() != null) {
      throw new IllegalStateException("This book has already been returned.");
    }

    rentalEntity.setReturnedAt(LocalDateTime.now());
    var returnedBookId = rentalEntity.getBookEntity().getId();
    bookService.incrementBookStock(returnedBookId);
    rentalRepository.save(rentalEntity);
    LOGGER.info("Book returned successfully: {}", rentalEntity);
    return rentalMapper.convertEntityToDto(rentalEntity);
  }
}
