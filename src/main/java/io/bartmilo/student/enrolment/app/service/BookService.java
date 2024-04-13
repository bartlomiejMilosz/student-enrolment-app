package io.bartmilo.student.enrolment.app.service;

import io.bartmilo.student.enrolment.app.domain.entity.BookEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface BookService {

    /**
     * Saves the specified book to the database.
     *
     * @param bookEntity The book to save to database.
     * @return The saved book.
     */
    BookEntity save(BookEntity bookEntity);

    /**
     * Returns all books in the database.
     *
     * @param pageable The pagination information.
     * @return A paginated list of books in the database.
     */
    Page<BookEntity> findAll(Pageable pageable);

    /**
     * Returns the book with the specified id.
     *
     * @param id ID of the book to retrieve.
     * @return The requested book if found.
     */
    Optional<BookEntity> findById(Long id);

    /**
     * Returns boolean of book in the database with the specified ID. Checks whether the book exists in the database.
     * @param id ID of the book to check.
     * @return The requested book existence.
     */
    boolean isExists(Long id);

    /**
     * Updates the specified book, identified by given ID.
     *
     * @param id         The ID of the book to update.
     * @param bookEntity The book to update.
     * @return The updated book.
     */
    BookEntity partialUpdate(Long id, BookEntity bookEntity);

    /**
     * Deletes the book with the specified ID.
     *
     * @param id The ID of the book to delete.
     */
    void delete(Long id);
}
