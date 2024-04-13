package io.bartmilo.student.enrolment.app.service;

import io.bartmilo.student.enrolment.app.domain.entity.RentalEntity;
import jakarta.persistence.EntityNotFoundException;

import java.time.LocalDateTime;

public interface RentalService {

    /**
     * Rents a book to a student based on the provided book and student IDs.
     * Ensures that the book is available for rental and that the student is eligible to rent the book.
     * Also, checks if the student's ID card is active.
     *
     * @param bookId the unique identifier of the book to be rented
     * @param studentId the unique identifier of the student renting the book
     * @param dueDate the due date for returning the rented book
     * @return a {@link RentalEntity} object containing details of the rental transaction,
     *         including book and student identifiers, the rental date, and the due date
     * @throws EntityNotFoundException if no student or book with the given ID exists
     * @throws IllegalStateException if the student's ID card is inactive
     * @throws IllegalArgumentException if the book is not available for rental
     */
    RentalEntity rentBook(Long bookId, Long studentId, LocalDateTime dueDate);

    /**
     * Returns a book that was previously rented. This method handles the logic to
     * mark a rented book as returned, updating the inventory and the rental records.
     *
     * @param rentalId the unique identifier of the rental record to be marked as returned,
     *                 corresponding to the transaction where a student previously rented a book
     * @return a {@link RentalEntity} object containing details of the rental transaction
     * including book and student identifiers, the rental date, and the due date
     * @throws EntityNotFoundException if no rental record with the given ID exists
     * @throws IllegalStateException   if the book has already been marked as returned
     */
    RentalEntity returnBook(Long rentalId);
}
