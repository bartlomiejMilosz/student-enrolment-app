package io.bartmilo.student.enrolment.app.domain.book.service;

import io.bartmilo.student.enrolment.app.domain.book.exception.BookNotFoundException;
import io.bartmilo.student.enrolment.app.domain.book.mapper.BookMapper;
import io.bartmilo.student.enrolment.app.domain.book.model.BookDto;
import io.bartmilo.student.enrolment.app.domain.book.model.BookEntity;
import io.bartmilo.student.enrolment.app.domain.book.repository.BookRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BookServiceImpl implements BookService {

  public static final String BOOK_NOT_FOUND_WITH_ID = "Book not found with ID: ";
  private static final Logger LOGGER = LoggerFactory.getLogger(BookServiceImpl.class);
  private final BookRepository bookRepository;
  private final BookMapper bookMapper;

  public BookServiceImpl(BookRepository bookRepository, BookMapper bookMapper) {
    this.bookRepository = bookRepository;
    this.bookMapper = bookMapper;
  }

  private static void checkBookStock(Long bookId, int amount, BookEntity bookEntity) {
    if (bookEntity.getStock() <= 0) {
      LOGGER.error("Book stock unavailable for book ID: {}", bookId);
      throw new IllegalArgumentException("Book is not available for rent.");
    }

    if (bookEntity.getStock() < amount) {
      throw new IllegalArgumentException("Insufficient book stock.");
    }
  }

  @Override
  @Transactional
  public BookDto save(BookDto bookDto) {
    LOGGER.info("Saving book to the database: {}", bookDto);
    var bookEntity = bookMapper.convertDtoToEntity(bookDto);
    var savedBookEntity = bookRepository.save(bookEntity);
    return bookMapper.convertEntityToDto(savedBookEntity);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<BookDto> findAll(Pageable pageable) {
    LOGGER.info("Fetching all books with pagination");
    var bookEntityPage = bookRepository.findAll(pageable);
    return bookEntityPage.map(bookMapper::convertEntityToDto);
  }

  @Override
  @Transactional(readOnly = true)
  public BookDto findById(Long id) {
    LOGGER.info("Finding book with ID: {}", id);
    return bookRepository
        .findById(id)
        .map(bookMapper::convertEntityToDto)
        .orElseThrow(() -> new BookNotFoundException(BOOK_NOT_FOUND_WITH_ID + id));
  }

  @Override
  @Transactional(readOnly = true)
  public boolean isExists(Long id) {
    LOGGER.info("Checking whether the book exists with ID: {}", id);
    return bookRepository.existsById(id);
  }

  @Override
  @Transactional
  public BookDto partialUpdate(Long id, BookDto bookDto) {
    LOGGER.info("Updating book with ID: {}", id);
    return bookRepository
        .findById(id)
        .map(existingBook -> updateExistingBook(bookDto, existingBook))
        .orElseThrow(() -> new BookNotFoundException(BOOK_NOT_FOUND_WITH_ID + id));
  }

  @Override
  @Transactional
  public void delete(Long id) {
    LOGGER.info("Deleting book with ID: {}", id);
    bookRepository.deleteById(id);
  }

  @Override
  @Transactional
  public BookDto decrementBookStock(Long bookId, int amount) {
    var bookEntity =
        bookRepository
            .findById(bookId)
            .orElseThrow(() -> new BookNotFoundException(BOOK_NOT_FOUND_WITH_ID + bookId));
    checkBookStock(bookId, amount, bookEntity);
    bookEntity.setStock(bookEntity.getStock() - amount);
    var savedBookEntity = bookRepository.save(bookEntity);
    return bookMapper.convertEntityToDto(savedBookEntity);
  }

  @Override
  @Transactional
  public BookDto decrementBookStock(Long bookId) {
    return this.decrementBookStock(bookId, 1);
  }

  @Override
  @Transactional
  public void returnBookToStock(BookDto bookDto) {
    var bookEntity = bookMapper.convertDtoToEntity(bookDto);
    bookEntity.setStock(bookEntity.getStock() + 1);
    bookRepository.save(bookEntity);
  }

  @Override
  @Transactional
  public void incrementBookStock(Long bookId) {
    BookEntity book =
        bookRepository
            .findById(bookId)
            .orElseThrow(() -> new BookNotFoundException("Book not found with ID: " + bookId));
    book.setStock(book.getStock() + 1);
    bookRepository.save(book);
  }

  private BookDto updateExistingBook(BookDto bookDto, BookEntity existingBook) {
    LOGGER.info("Found book to update: {}", existingBook);

    if (bookDto.getBookAuthor() != null) {
      existingBook.setBookAuthor(bookDto.getBookAuthor());
    }
    if (bookDto.getTitle() != null) {
      existingBook.setTitle(bookDto.getTitle());
    }
    if (bookDto.getIsbn() != null) {
      existingBook.setIsbn(bookDto.getIsbn());
    }
    if (bookDto.getCreatedAt() != null) {
      existingBook.setCreatedAt(bookDto.getCreatedAt());
    }
    if (bookDto.getStock() != null) {
      existingBook.setStock(bookDto.getStock());
    }

    var updatedBook = bookRepository.save(existingBook);
    LOGGER.info("Updated book: {}", updatedBook);
    return bookMapper.convertEntityToDto(updatedBook);
  }
}
