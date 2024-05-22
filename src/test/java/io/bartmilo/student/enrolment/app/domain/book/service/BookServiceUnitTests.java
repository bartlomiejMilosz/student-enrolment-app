/*
package io.bartmilo.student.enrolment.app.domain.book.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import io.bartmilo.student.enrolment.app.TestDataUtil;
import io.bartmilo.student.enrolment.app.domain.book.model.BookEntity;
import io.bartmilo.student.enrolment.app.domain.book.repository.BookRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
class BookServiceUnitTests {

  @Mock private BookRepository bookRepositoryMock;

  private BookService bookService;

  @BeforeEach
  void setUp() {
    this.bookService = new BookServiceImpl(bookRepositoryMock);
  }

  @Test
  void whenBookIsSaved_ThanCanBeRecalled() {
    var book = TestDataUtil.createSingleTestBookEntity();
    when(bookRepositoryMock.save(book)).thenReturn(book);

    var savedBook = bookService.save(book);

    verify(bookRepositoryMock, times(1)).save(book);
    assertAll(() -> assertThat(savedBook).isEqualTo(book));
  }

  @Test
  void whenListOfBooksIsSaved_ThanCanBeRecalled() {
    var bookList = TestDataUtil.createListOfTestBookEntities();
    for (var book : bookList) {
      when(bookRepositoryMock.save(book)).thenReturn(book);
    }

    bookList.forEach(bookService::save);

    for (var book : bookList) {
      verify(bookRepositoryMock).save(book);
      assertThat(bookService.save(book)).isEqualTo(book);
    }
  }

  @Test
  void whenFindAll_ThenReturnPageOfBooks() {
    var pageable = PageRequest.of(0, 10);
    var bookList = TestDataUtil.createListOfTestBookEntities();
    var expectedPage = new PageImpl<>(bookList, pageable, bookList.size());
    when(bookRepositoryMock.findAll(pageable)).thenReturn(expectedPage);

    var result = bookService.findAll(pageable);

    assertAll(
        () -> assertThat(result.getContent()).isEqualTo(bookList),
        () -> assertThat(result.getTotalElements()).isEqualTo(bookList.size()));
  }

  @Test
  void whenFindById_ThenReturnBook() {
    var book = TestDataUtil.createSingleTestBookEntity();
    when(bookRepositoryMock.findById(book.getId())).thenReturn(Optional.of(book));

    var result = bookService.findById(book.getId());

    assertThat(result).isPresent().contains(book);
  }

  @Test
  void whenCheckIfBookExists_ThenReturnTrueOrFalse() {
    Long bookId = 1L;
    when(bookRepositoryMock.existsById(bookId)).thenReturn(true);

    boolean exists = !bookService.isExists(bookId);

    assertThat(exists).isTrue();
  }

  @Test
  void whenPartialUpdate_ThenBookIsUpdated() {
    Long bookId = 1L;
    var book = TestDataUtil.createSingleTestBookEntity();
    book.setId(bookId);
    var updateInfo = new BookEntity();
    updateInfo.setTitle("UpdatedTitle");

    when(bookRepositoryMock.findById(bookId)).thenReturn(Optional.of(book));
    when(bookRepositoryMock.save(any(BookEntity.class))).thenReturn(book);

    var updatedBook = bookService.partialUpdate(bookId, updateInfo);

    verify(bookRepositoryMock).save(book); // Verify save was called on the original student
    assertThat(updatedBook.getTitle()).isEqualTo(updateInfo.getTitle());
  }

  @Test
  void whenDelete_ThenRepositoryDeleteIsCalled() {
    Long bookId = 1L;
    doNothing().when(bookRepositoryMock).deleteById(bookId);

    bookService.delete(bookId);

    verify(bookRepositoryMock, times(1)).deleteById(bookId);
    assertThat(bookService.findById(bookId)).isEmpty();
  }
}
*/
