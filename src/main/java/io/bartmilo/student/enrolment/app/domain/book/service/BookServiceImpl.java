package io.bartmilo.student.enrolment.app.domain.book.service;

import io.bartmilo.student.enrolment.app.domain.book.exception.BookNotFoundException;
import io.bartmilo.student.enrolment.app.domain.book.model.BookDto;
import io.bartmilo.student.enrolment.app.domain.book.model.BookEntity;
import io.bartmilo.student.enrolment.app.domain.book.repository.BookRepository;
import io.bartmilo.student.enrolment.app.util.DomainMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BookServiceImpl implements BookService {
  private static final Logger LOGGER = LoggerFactory.getLogger(BookServiceImpl.class);
  private final BookRepository bookRepository;
  private final DomainMapper domainMapper;

  public BookServiceImpl(BookRepository bookRepository, DomainMapper domainMapper) {
    this.bookRepository = bookRepository;
    this.domainMapper = domainMapper;
  }

  @Override
  @Transactional
  public BookDto save(BookDto bookDto) {
    LOGGER.info("Saving book to the database: {}", bookDto);
    var bookEntity = domainMapper.convertDtoToEntity(bookDto, BookEntity.class);
    var savedBookEntity = bookRepository.save(bookEntity);
    return domainMapper.convertEntityToDto(savedBookEntity, BookDto.class);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<BookDto> findAll(Pageable pageable) {
    LOGGER.info("Fetching all books with pagination");
    var bookEntityPage = bookRepository.findAll(pageable);
    return bookEntityPage.map(entity -> domainMapper.convertEntityToDto(entity, BookDto.class));
  }

  @Override
  @Transactional(readOnly = true)
  public BookDto findById(Long id) {
    LOGGER.info("Finding book with ID: {}", id);
    return bookRepository
        .findById(id)
        .map(bookEntity -> domainMapper.convertEntityToDto(bookEntity, BookDto.class))
        .orElseThrow(() -> new BookNotFoundException("Book not found with ID: " + id));
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
        .orElseThrow(() -> new BookNotFoundException("Book not found with ID: " + id));
  }

  @Override
  @Transactional
  public void delete(Long id) {
    LOGGER.info("Deleting book with ID: {}", id);
    bookRepository.deleteById(id);
  }

  private BookDto updateExistingBook(BookDto bookDto, BookEntity existingBook) {
    LOGGER.info("Found book to update: {}", existingBook);

    if (bookDto.bookAuthor() != null) {
      existingBook.setBookAuthor(bookDto.bookAuthor());
    }
    if (bookDto.title() != null) {
      existingBook.setTitle(bookDto.title());
    }
    if (bookDto.isbn() != null) {
      existingBook.setIsbn(bookDto.isbn());
    }
    if (bookDto.createdAt() != null) {
      existingBook.setCreatedAt(bookDto.createdAt());
    }
    if (bookDto.stock() != null) {
      existingBook.setStock(bookDto.stock());
    }

    var updatedBook = bookRepository.save(existingBook);
    LOGGER.info("Updated book: {}", updatedBook);
    return domainMapper.convertEntityToDto(updatedBook, BookDto.class);
  }
}
