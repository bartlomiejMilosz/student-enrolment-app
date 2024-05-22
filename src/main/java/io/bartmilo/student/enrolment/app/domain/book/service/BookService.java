package io.bartmilo.student.enrolment.app.domain.book.service;

import io.bartmilo.student.enrolment.app.domain.book.model.BookDto;
import io.bartmilo.student.enrolment.app.domain.book.model.BookEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface BookService {

    /**
     * Saves the specified book to the database.
     *
     * @param bookDto The book to save to database.
     * @return The saved book.
     */
    BookDto save(BookDto bookDto);

    /**
     * Returns all books in the database.
     *
     * @param pageable The pagination information.
     * @return A paginated list of books in the database.
     */
    Page<BookDto> findAll(Pageable pageable);

    /**
     * Returns the book with the specified id.
     *
     * @param id ID of the book to retrieve.
     * @return The requested book if found.
     */
    BookDto findById(Long id);

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
     * @param bookDto The book to update.
     * @return The updated book.
     */
    BookDto partialUpdate(Long id, BookDto bookDto);

    /**
     * Deletes the book with the specified ID.
     *
     * @param id The ID of the book to delete.
     */
    void delete(Long id);
}
