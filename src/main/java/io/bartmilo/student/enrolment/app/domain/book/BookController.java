package io.bartmilo.student.enrolment.app.domain.book;

import io.bartmilo.student.enrolment.app.domain.book.mapper.BookMapper;
import io.bartmilo.student.enrolment.app.domain.book.model.BookDto;
import io.bartmilo.student.enrolment.app.domain.book.model.BookRequest;
import io.bartmilo.student.enrolment.app.domain.book.model.BookResponse;
import io.bartmilo.student.enrolment.app.domain.book.service.BookService;
import java.net.URI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/books")
public class BookController {

  private static final Logger LOGGER = LoggerFactory.getLogger(BookController.class);
  private final BookService bookService;
  private final BookMapper bookMapper;

  public BookController(BookService bookService, BookMapper bookMapper) {
    this.bookService = bookService;
    this.bookMapper = bookMapper;
  }

  @GetMapping
  public ResponseEntity<Page<BookResponse>> getAllBooks(
      @PageableDefault(size = 10) Pageable pageable) {
    LOGGER.info("Request to get all books");
    var bookDtoPage = bookService.findAll(pageable);
    LOGGER.info("Books retrieved with pagination");
    var bookResponsePage = bookDtoPage.map(bookMapper::convertDtoToResponse);
    return ResponseEntity.ok(bookResponsePage);
  }

  @GetMapping("/{id}")
  public ResponseEntity<BookResponse> getBookById(@PathVariable Long id) {
    LOGGER.info("Request to get book by ID: {}", id);
    var bookDto = bookService.findById(id);
    LOGGER.info("Book retrieved successfully: {}", bookDto);
    var bookResponse = bookMapper.convertDtoToResponse(bookDto);
    return ResponseEntity.ok(bookResponse);
  }

  @PostMapping
  public ResponseEntity<BookResponse> createBook(@RequestBody BookRequest bookRequest) {
    LOGGER.info("Request to create book: {}", bookRequest);
    var bookDto = bookMapper.convertRequestToDto(bookRequest);
    var savedBookDto = bookService.save(bookDto);

    URI location =
        ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(savedBookDto.getId())
            .toUri();
    LOGGER.info("Book created successfully: {}", savedBookDto);
    var bookResponse = bookMapper.convertDtoToResponse(savedBookDto);
    return ResponseEntity.created(location).body(bookResponse);
  }

  @PutMapping("/{id}")
  public ResponseEntity<BookResponse> updateBook(
      @PathVariable Long id, @RequestBody BookDto bookDto) {
    LOGGER.info("Request to update book with ID: {}", id);
    if (!bookService.isExists(id)) {
      LOGGER.error("Attempted to update a non-existent book with ID: {}", id);
      return ResponseEntity.notFound().build();
    }
    var updatedBookDto = bookService.partialUpdate(id, bookDto);
    LOGGER.info("Book updated successfully: {}", updatedBookDto);
    var bookResponse = bookMapper.convertDtoToResponse(updatedBookDto);
    return ResponseEntity.ok(bookResponse);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
    LOGGER.info("Request to delete book with ID: {}", id);
    if (!bookService.isExists(id)) {
      LOGGER.error("Attempted to delete a non-existent book with ID: {}", id);
      return ResponseEntity.notFound().build();
    }
    bookService.delete(id);
    LOGGER.info("Book deleted successfully with ID: {}", id);
    return ResponseEntity.noContent().build();
  }
}
