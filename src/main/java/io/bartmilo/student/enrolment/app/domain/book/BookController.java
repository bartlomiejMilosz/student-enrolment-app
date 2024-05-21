package io.bartmilo.student.enrolment.app.domain.book;

import io.bartmilo.student.enrolment.app.domain.book.model.BookDto;
import io.bartmilo.student.enrolment.app.domain.book.model.BookEntity;
import io.bartmilo.student.enrolment.app.util.ModelMapper;
import io.bartmilo.student.enrolment.app.domain.book.service.BookService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/books")
public class BookController {

    private static final Logger LOGGER = LoggerFactory.getLogger(BookController.class);
    private final BookService bookService;
    private final ModelMapper<BookEntity, BookDto> bookModelMapper;

    @Autowired
    public BookController(BookService bookService, ModelMapper<BookEntity, BookDto> bookModelMapper) {
        this.bookService = bookService;
        this.bookModelMapper = bookModelMapper;
    }

    /**
     * Creates a new book entry in the database.
     * @param bookDto the book DTO to create a new book.
     * @return ResponseEntity containing the created book.
     */
    @PostMapping
    public ResponseEntity<BookDto> createBook(@RequestBody BookDto bookDto) {
        LOGGER.info("Request to create book: {}", bookDto);
        BookEntity bookEntity = bookModelMapper.mapTo(bookDto);
        BookEntity savedBook = bookService.save(bookEntity);
        BookDto savedBookDto = bookModelMapper.mapFrom(savedBook);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedBook.getId())
                .toUri();

        LOGGER.info("Book created successfully: {}", savedBookDto);
        return ResponseEntity
                .created(location)
                .body(savedBookDto);
    }

    /**
     * Retrieves a book by its ID.
     * @param id the ID of the book to retrieve.
     * @return ResponseEntity containing the requested book.
     */
    @GetMapping("/{id}")
    public ResponseEntity<BookDto> getBookById(@PathVariable Long id) {
        LOGGER.info("Request to get book by ID: {}", id);
        BookEntity book = bookService.findById(id)
                .orElseThrow(() -> {
            LOGGER.error("Book not found with ID: {}", id);
            return new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found with ID: " + id);
        });

        BookDto bookDto = bookModelMapper.mapFrom(book);
        LOGGER.info("Book retrieved successfully: {}", bookDto);
        return ResponseEntity.ok(bookDto);
    }

    /**
     * Retrieves all books with pagination.
     * @param pageable the pagination information.
     * @return ResponseEntity containing a page of books.
     */
    @GetMapping
    public ResponseEntity<Page<BookDto>> getAllBooks(@PageableDefault(size = 10) Pageable pageable) {
        LOGGER.info("Request to get all books");
        Page<BookEntity> bookPage = bookService.findAll(pageable);
        Page<BookDto> bookDtoPage = bookPage.map(bookModelMapper::mapFrom);
        LOGGER.info("Books retrieved with pagination");
        return ResponseEntity.ok(bookDtoPage);
    }

    /**
     * Updates an existing book by ID.
     * @param id the ID of the book to update.
     * @param bookDto the updated book DTO.
     * @return ResponseEntity containing the updated book.
     */
    @PutMapping("/{id}")
    public ResponseEntity<BookDto> updateBook(@PathVariable Long id, @RequestBody BookDto bookDto) {
        LOGGER.info("Request to update book with ID: {}", id);
        if (bookService.isExists(id)) {
            LOGGER.error("Attempted to update a non-existent book with ID: {}", id);
            return ResponseEntity.notFound().build();
        }
        bookDto.setId(id);
        BookEntity updatedBook = bookService.partialUpdate(id, bookModelMapper.mapTo(bookDto));
        LOGGER.info("Book updated successfully: {}", updatedBook);
        return ResponseEntity
                .ok(bookModelMapper.mapFrom(updatedBook));
    }

    /**
     * Deletes a book by its ID.
     * @param id the ID of the book to delete.
     * @return ResponseEntity indicating success or failure.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        LOGGER.info("Request to delete book with ID: {}", id);
        if (bookService.isExists(id)) {
            LOGGER.error("Attempted to delete a non-existent book with ID: {}", id);
            return ResponseEntity.notFound().build();
        }
        bookService.delete(id);
        LOGGER.info("Book deleted successfully with ID: {}", id);
        return ResponseEntity
                .noContent()
                .build();
    }
}
