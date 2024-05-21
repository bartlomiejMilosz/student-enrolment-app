package io.bartmilo.student.enrolment.app.service;

import io.bartmilo.student.enrolment.app.TestDataUtil;
import io.bartmilo.student.enrolment.app.domain.book.model.BookEntity;
import io.bartmilo.student.enrolment.app.domain.book.repository.BookRepository;
import io.bartmilo.student.enrolment.app.domain.book.service.BookService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class BookServiceUnitTests {

    @MockBean
    private BookRepository bookRepositoryMock;

    @Autowired
    private BookService bookService;

    @Test
    void whenBookIsSaved_ThanCanBeRecalled() {
        // Given
        var book = TestDataUtil.createSingleTestBookEntity();
        when(bookRepositoryMock.save(book)).thenReturn(book);

        // When
        var savedBook = bookService.save(book);

        // Then
        verify(bookRepositoryMock, times(1)).save(book);
        assertAll(
                () -> assertThat(savedBook).isEqualTo(book)
        );
    }

    @Test
    void whenListOfBooksIsSaved_ThanCanBeRecalled() {
        // Given
        var bookList = TestDataUtil.createListOfTestBookEntities();
        for (var book : bookList) {
            when(bookRepositoryMock.save(book)).thenReturn(book);
        }

        // When
        bookList.forEach(bookService::save);

        // Then
        for (var book : bookList) {
            verify(bookRepositoryMock).save(book);
            assertThat(bookService.save(book)).isEqualTo(book);
        }
    }

    @Test
    void whenFindAll_ThenReturnPageOfBooks() {
        // Given
        var pageable = PageRequest.of(0, 10);
        var bookList = TestDataUtil.createListOfTestBookEntities();
        var expectedPage = new PageImpl<>(bookList, pageable, bookList.size());
        when(bookRepositoryMock.findAll(pageable)).thenReturn(expectedPage);

        // When
        var result = bookService.findAll(pageable);

        // Then
        assertAll(
                () -> assertThat(result.getContent()).isEqualTo(bookList),
                () -> assertThat(result.getTotalElements()).isEqualTo(bookList.size())
        );
    }

    @Test
    void whenFindById_ThenReturnBook() {
        // Given
        var book = TestDataUtil.createSingleTestBookEntity();
        when(bookRepositoryMock.findById(book.getId())).thenReturn(Optional.of(book));

        // When
        var result = bookService.findById(book.getId());

        // Then
        assertThat(result).isPresent().contains(book);
    }

    @Test
    void whenCheckIfBookExists_ThenReturnTrueOrFalse() {
        // Given
        Long bookId = 1L;
        when(bookRepositoryMock.existsById(bookId)).thenReturn(true);

        // When
        boolean exists = !bookService.isExists(bookId);

        // Then
        assertThat(exists).isTrue();
    }

    @Test
    void whenPartialUpdate_ThenBookIsUpdated() {
        // Given
        Long bookId = 1L;
        var book = TestDataUtil.createSingleTestBookEntity();
        book.setId(bookId);
        var updateInfo = new BookEntity();
        updateInfo.setTitle("UpdatedTitle");

        when(bookRepositoryMock.findById(bookId)).thenReturn(Optional.of(book));
        when(bookRepositoryMock.save(any(BookEntity.class))).thenReturn(book);

        // When
        var updatedBook = bookService.partialUpdate(bookId, updateInfo);

        // Then
        verify(bookRepositoryMock).save(book); // Verify save was called on the original student
        assertThat(updatedBook.getTitle()).isEqualTo(updateInfo.getTitle());
    }

    @Test
    void whenDelete_ThenRepositoryDeleteIsCalled() {
        // Given
        Long bookId = 1L;
        doNothing().when(bookRepositoryMock).deleteById(bookId);

        // When
        bookService.delete(bookId);

        // Then
        verify(bookRepositoryMock, times(1)).deleteById(bookId);
        assertThat(bookService.findById(bookId)).isEmpty();
    }
}