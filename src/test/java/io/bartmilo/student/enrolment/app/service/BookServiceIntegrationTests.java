package io.bartmilo.student.enrolment.app.service;

import io.bartmilo.student.enrolment.app.TestDataUtil;
import io.bartmilo.student.enrolment.app.domain.entity.BookEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class BookServiceIntegrationTests {

    private final BookService bookService;

    @Autowired
    public BookServiceIntegrationTests(BookService bookService) {
        this.bookService = bookService;
    }

    @Test
    void whenBookIsSaved_ThanCanBeRecalled() {
        // Given
        var book = TestDataUtil.createSingleTestBookEntity();
        bookService.save(book);

        // When
        var result = bookService.findById(book.getId());

        // Then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(book);
    }

    @Test
    void whenListOfBooksIsSaved_ThanCanBeRecalled() {
        // Given
        var bookList = TestDataUtil.createListOfTestBookEntities();
        bookList.forEach(bookService::save);

        int page = 0;
        int size = 3;
        var pageable = PageRequest.of(page, size);

        // When
        var result = bookService.findAll(pageable);

        // Then
        assertAll(
                // Check that the page content has the correct number of students
                () -> assertThat(result.getContent()).hasSize(size),

                // Check the page number
                () -> assertThat(result.getNumber()).isEqualTo(page),

                // Check the page size
                () -> assertThat(result.getSize()).isEqualTo(size),

                // Check the total number of elements
                () -> assertThat(result.getTotalElements()).isEqualTo(bookList.size()),

                // Check the first student in the page against the first student in the list
                () -> {
                    var bookFromPage = result.getContent().get(0);
                    var bookFromList = bookList.get(0);
                    assertThat(bookFromPage).isEqualTo(bookFromList);
                }
        );
    }

    @Test
    void whenFindById_ThenReturnBook() {
        // Given
        var book = TestDataUtil.createSingleTestBookEntity();
        bookService.save(book);

        // When
        var foundBook = bookService.findById(book.getId());

        // Then
        assertThat(foundBook).isPresent().contains(book);
    }

    @Test
    void whenCheckExistence_ThenConfirm() {
        // Given
        var book = TestDataUtil.createSingleTestBookEntity();
        bookService.save(book);

        // When
        boolean exists = !bookService.isExists(book.getId());

        // Then
        assertTrue(exists);
    }

    @Test
    void whenPartialUpdate_ThenUpdateFields() {
        // Given
        var book = TestDataUtil.createSingleTestBookEntity();
        book = bookService.save(book);
        var updatedInfo = new BookEntity();
        updatedInfo.setTitle("UpdatedTitle");

        // When
        var updatedBook = bookService.partialUpdate(book.getId(), updatedInfo);

        // Then
        assertThat(updatedBook.getTitle()).isEqualTo("UpdatedTitle");
    }

    @Test
    void whenDelete_ThenBookIsNotPresent() {
        // Given
        var book = TestDataUtil.createSingleTestBookEntity();
        book = bookService.save(book);

        // When
        bookService.delete(book.getId());

        // Then
        var result = bookService.findById(book.getId());
        assertThat(result).isEmpty();
    }
}
