package io.bartmilo.student.enrolment.app.service.impl;

import io.bartmilo.student.enrolment.app.domain.entity.BookEntity;
import io.bartmilo.student.enrolment.app.domain.entity.RentalEntity;
import io.bartmilo.student.enrolment.app.domain.entity.StudentEntity;
import io.bartmilo.student.enrolment.app.domain.entity.StudentIdCardEntity;
import io.bartmilo.student.enrolment.app.repository.RentalRepository;
import io.bartmilo.student.enrolment.app.service.BookService;
import io.bartmilo.student.enrolment.app.service.RentalService;
import io.bartmilo.student.enrolment.app.service.StudentService;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class RentalServiceImpl implements RentalService {
    private static final Logger LOGGER = LoggerFactory.getLogger(RentalServiceImpl.class);

    private final StudentService studentService;
    private final BookService bookService;
    private final RentalRepository rentalRepository;

    public RentalServiceImpl(
            StudentService studentService,
            BookService bookService,
            RentalRepository rentalRepository
    ) {
        this.studentService = studentService;
        this.bookService = bookService;
        this.rentalRepository = rentalRepository;
    }

    @Override
    @Transactional
    public RentalEntity rentBook(Long bookId, Long studentId, LocalDateTime dueDate) {
        LOGGER.info(
                "Attempting to rent book with ID: {} for student with ID: {} due by {}",
                bookId,
                studentId,
                dueDate
        );
        StudentEntity student = studentService.findById(studentId)
                .orElseThrow(() -> new EntityNotFoundException("Student not found with ID: " + studentId));
        LOGGER.debug("Student details retrieved: {}", student);

        // Check the status of the student's ID card
        if (
                student.getStudentIdCardEntity() == null ||
                student.getStudentIdCardEntity().getStatus() != StudentIdCardEntity.CardStatus.ACTIVE
        ) {
            LOGGER.warn("Attempt to rent with inactive ID card by student ID: {}", studentId);
            throw new IllegalStateException("Student's ID card is not active.");
        }

        BookEntity book = bookService.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book not found with ID: " + bookId));
        LOGGER.debug("Book details retrieved: {}", book);

        if (book.getStock() == null || book.getStock() <= 0) {
            LOGGER.warn("Book stock unavailable for book ID: {}", bookId);
            throw new IllegalArgumentException("Book is not available for rent.");
        }

        book.setStock(book.getStock() - 1); // Decrement the stock
        bookService.save(book); // Save the updated book stock
        LOGGER.info("Updated book stock for book ID: {}. New stock: {}", bookId, book.getStock());

        RentalEntity rentalEntity = new RentalEntity();
        rentalEntity.setRentedAt(LocalDateTime.now());
        rentalEntity.setBookEntity(book);
        rentalEntity.setStudentEntity(student);
        rentalEntity.setDueDate(dueDate);
        LOGGER.debug("Created rental record: {}", rentalEntity);

        LOGGER.info("Saving rental to the database.");
        return rentalRepository.save(rentalEntity);
    }

    @Override
    @Transactional
    public RentalEntity returnBook(Long rentalId) {
        RentalEntity rental = rentalRepository.findById(rentalId)
                .orElseThrow(() -> new EntityNotFoundException("Rental not found with ID: " + rentalId));

        if (rental.getReturnedAt() != null) {
            throw new IllegalStateException("This book has already been returned.");
        }

        BookEntity book = rental.getBookEntity();
        book.setStock(book.getStock() + 1);  // Increment stock on return
        bookService.save(book);

        rental.setReturnedAt(LocalDateTime.now());
        rentalRepository.save(rental);

        LOGGER.info("Book returned successfully: {}", rental);
        return rental;
    }

}
