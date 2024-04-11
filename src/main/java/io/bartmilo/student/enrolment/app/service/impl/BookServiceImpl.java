package io.bartmilo.student.enrolment.app.service.impl;

import io.bartmilo.student.enrolment.app.domain.entity.BookEntity;
import io.bartmilo.student.enrolment.app.repository.BookRepository;
import io.bartmilo.student.enrolment.app.service.BookService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class BookServiceImpl implements BookService {
    private static final Logger LOGGER = LoggerFactory.getLogger(BookServiceImpl.class);
    private final BookRepository bookRepository;

    @Autowired
    public BookServiceImpl(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    @Transactional
    public BookEntity save(BookEntity bookEntity) {
        LOGGER.info("Saving book to the database: {}", bookEntity);
        return bookRepository.save(bookEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BookEntity> findAll(Pageable pageable) {
        LOGGER.info("Fetching all books with pagination");
        return bookRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<BookEntity> findById(Long id) {
        LOGGER.info("Finding book with ID: {}", id);
        return bookRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isExists(Long id) {
        LOGGER.info("Checking whether the book exists with ID: {}", id);
        return !bookRepository.existsById(id);
    }

    @Override
    @Transactional
    public BookEntity partialUpdate(Long id, BookEntity bookEntity) {
        LOGGER.info("Make sure the book you want to update has specified id: {}", id);
        bookEntity.setId(id);

        LOGGER.info("Update book: {}, with id: {}", bookEntity, id);
        return bookRepository.findById(id).map(existingBook -> {
            Optional.ofNullable(bookEntity.getBookAuthor())
                    .ifPresent(existingBook::setBookAuthor);
            Optional.ofNullable(bookEntity.getTitle())
                    .ifPresent(existingBook::setTitle);
            Optional.ofNullable(bookEntity.getIsbn())
                    .ifPresent(existingBook::setIsbn);
            Optional.ofNullable(bookEntity.getCreatedAt())
                    .ifPresent(existingBook::setCreatedAt);
            Optional.ofNullable(bookEntity.getStock())
                    .ifPresent(existingBook::setStock);
            return bookRepository.save(existingBook);
        }).orElseThrow(() -> new RuntimeException("Book does not exist"));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        LOGGER.info("Deleting book with ID: {}", id);
        bookRepository.deleteById(id);
    }
}
