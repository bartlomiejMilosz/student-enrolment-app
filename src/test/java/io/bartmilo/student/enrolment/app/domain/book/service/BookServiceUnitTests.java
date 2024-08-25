package io.bartmilo.student.enrolment.app.domain.book.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import io.bartmilo.student.enrolment.app.TestDataUtil;
import io.bartmilo.student.enrolment.app.domain.book.exception.BookNotFoundException;
import io.bartmilo.student.enrolment.app.domain.book.mapper.BookMapper;
import io.bartmilo.student.enrolment.app.domain.book.model.BookDto;
import io.bartmilo.student.enrolment.app.domain.book.model.BookEntity;
import io.bartmilo.student.enrolment.app.domain.book.repository.BookRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
class BookServiceUnitTests {

  @Mock private BookRepository bookRepositoryMock;
  @Mock private BookMapper bookMapperMock;
  @Captor private ArgumentCaptor<BookEntity> bookEntityArgumentCaptor;

  private BookService bookService;

  @BeforeEach
  void setUp() {
    this.bookService = new BookServiceImpl(bookRepositoryMock, bookMapperMock);
  }

  @Test
  void whenBookIsSaved_ThanCanBeRecalled() {
    var bookEntity = TestDataUtil.createSingleTestBookEntity();
    var bookDto = TestDataUtil.createSingleTestBookDto();
    when(bookMapperMock.convertDtoToEntity(bookDto)).thenReturn(bookEntity);
    when(bookMapperMock.convertEntityToDto(bookEntity)).thenReturn(bookDto);
    when(bookRepositoryMock.save(bookEntity)).thenReturn(bookEntity);

    var savedBookDto = bookService.save(bookDto);

    verify(bookRepositoryMock).save(bookEntity);
    assertAll(
        () -> assertThat(savedBookDto).isEqualTo(bookDto),
        () -> assertThat(savedBookDto.getTitle()).isEqualTo(bookDto.getTitle()),
        () -> assertThat(savedBookDto.getBookAuthor()).isEqualTo(bookDto.getBookAuthor()));
  }

  @Test
  void whenListOfBooksIsSaved_ThenCanBeRecalled() {
    var bookEntityList = TestDataUtil.createListOfTestBookEntity();
    var bookDtoList = TestDataUtil.createListOfTestBookDto();
    for (int i = 0; i < bookDtoList.size(); i++) {
      var bookDto = bookDtoList.get(i);
      var bookEntity = bookEntityList.get(i);
      when(bookMapperMock.convertDtoToEntity(bookDto)).thenReturn(bookEntity);
      when(bookMapperMock.convertEntityToDto(bookEntity)).thenReturn(bookDto);
      when(bookRepositoryMock.save(bookEntity)).thenReturn(bookEntity);
    }

    var savedDtos = bookDtoList.stream().map(bookService::save).toList();

    for (int i = 0; i < bookDtoList.size(); i++) {
      var expectedDto = bookDtoList.get(i);
      var savedDto = savedDtos.get(i);
      assertThat(savedDto).isEqualTo(expectedDto);
    }

    for (BookEntity bookEntity : bookEntityList) {
      verify(bookRepositoryMock, times(1)).save(bookEntity);
    }
  }

  @Test
  void whenFindAll_ThenReturnPageOfBooks() {
    var pageable = PageRequest.of(0, 10);
    var bookEntityList = TestDataUtil.createListOfTestBookEntity();
    var bookDtoList = TestDataUtil.createListOfTestBookDto();
    var expectedPageOfEntities = new PageImpl<>(bookEntityList, pageable, bookEntityList.size());
    when(bookRepositoryMock.findAll(pageable)).thenReturn(expectedPageOfEntities);
    for (int i = 0; i < bookDtoList.size(); i++) {
      var bookEntity = bookEntityList.get(i);
      var bookDto = bookDtoList.get(i);
      when(bookMapperMock.convertEntityToDto(bookEntity)).thenReturn(bookDto);
    }

    var result = bookService.findAll(pageable);

    assertAll(
        () -> assertThat(result.getContent()).isEqualTo(bookDtoList),
        () -> assertThat(result.getTotalElements()).isEqualTo(bookDtoList.size()));
  }

  @Test
  void whenFindById_ThenReturnBook() {
    var bookEntity = TestDataUtil.createSingleTestBookEntity();
    var id = bookEntity.getId();
    var bookDto = TestDataUtil.createSingleTestBookDto();
    when(bookMapperMock.convertEntityToDto(bookEntity)).thenReturn(bookDto);
    when(bookRepositoryMock.findById(id)).thenReturn(Optional.of(bookEntity));

    var result = bookService.findById(id);

    verify(bookRepositoryMock).findById(id);
    verify(bookMapperMock).convertEntityToDto(bookEntity);
    assertThat(result).isEqualTo(bookDto);
  }

  @Test
  void whenCheckIfBookExists_ThenReturnTrueOrFalse() {
    var bookId = 1L;
    when(bookRepositoryMock.existsById(bookId)).thenReturn(true);

    var exists = bookService.isExists(bookId);

    verify(bookRepositoryMock).existsById(bookId);
    assertThat(exists).isTrue();
  }

  @Test
  void whenPartialUpdate_ThenBookIsUpdated() {
    var bookId = 1L;
    var existingBookEntity = TestDataUtil.createSingleTestBookEntity();
    existingBookEntity.setTitle("UPDATED TITLE");
    var bookDto = TestDataUtil.createSingleTestBookDto();
    bookDto.setTitle("UPDATED TITLE");

    when(bookRepositoryMock.findById(bookId)).thenReturn(Optional.of(existingBookEntity));
    when(bookRepositoryMock.save(any(BookEntity.class))).thenReturn(existingBookEntity);
    when(bookMapperMock.convertEntityToDto(existingBookEntity)).thenReturn(bookDto);

    bookService.partialUpdate(bookId, bookDto);

    verify(bookRepositoryMock).findById(bookId);
    verify(bookRepositoryMock).save(bookEntityArgumentCaptor.capture());
    verify(bookMapperMock).convertEntityToDto(existingBookEntity);
    assertThat(bookEntityArgumentCaptor.getValue().getTitle()).isEqualTo("UPDATED TITLE");
  }

  @Test
  void whenDelete_ThenRepositoryDeleteIsCalled() {
    var bookId = 1L;
    doNothing().when(bookRepositoryMock).deleteById(bookId);

    bookService.delete(bookId);

    verify(bookRepositoryMock).deleteById(bookId);
    assertThrows(BookNotFoundException.class, () -> bookService.findById(bookId));
  }

  @Test
  void whenDecrementBookStock_ThenStockIsDecremented() {
    var bookId = 1L;
    var decrementAmount = 1;
    var bookEntity = TestDataUtil.createSingleTestBookEntity();
    bookEntity.setStock(10);  // Initial stock
    var bookDto = TestDataUtil.createSingleTestBookDto();
    bookDto.setStock(9);  // Expected stock after decrement

    when(bookRepositoryMock.findById(bookId)).thenReturn(Optional.of(bookEntity));
    when(bookRepositoryMock.save(bookEntity)).thenReturn(bookEntity);
    when(bookMapperMock.convertEntityToDto(bookEntity)).thenReturn(bookDto);

    var result = bookService.decrementBookStock(bookId, decrementAmount);

    verify(bookRepositoryMock).save(bookEntity);
    assertThat(result.getStock()).isEqualTo(9);
    assertThat(result).isEqualTo(bookDto);
  }

  @Test
  void whenDecrementBookStockWithDefault_ThenStockIsDecrementedByOne() {
    var bookId = 1L;
    var bookEntity = new BookEntity();
    bookEntity.setId(bookId);
    bookEntity.setStock(10);
    when(bookRepositoryMock.findById(bookId)).thenReturn(Optional.of(bookEntity));
    when(bookRepositoryMock.save(any(BookEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

    bookService.decrementBookStock(bookId);

    verify(bookRepositoryMock).save(bookEntityArgumentCaptor.capture());
    var capturedBook = bookEntityArgumentCaptor.getValue();
    assertEquals(9, capturedBook.getStock(), "The stock should be decremented by 1");
  }

  @Test
  void whenReturnBookToStock_ThenStockIsIncremented() {
    var bookId = 1L;
    var bookEntity = new BookEntity();
    bookEntity.setId(bookId);
    bookEntity.setStock(10);

    var bookDto = new BookDto();
    bookDto.setId(bookId);
    bookDto.setStock(10);

    when(bookMapperMock.convertDtoToEntity(any(BookDto.class))).thenReturn(bookEntity);
    when(bookRepositoryMock.save(any(BookEntity.class))).thenReturn(bookEntity);

    bookService.returnBookToStock(bookDto);

    var bookEntityCaptor = ArgumentCaptor.forClass(BookEntity.class);
    verify(bookRepositoryMock).save(bookEntityCaptor.capture());
    var savedBook = bookEntityCaptor.getValue();
    assertThat(savedBook.getStock()).isEqualTo(11);
  }

  @Test
  void testIncrementBookStock() {
    var bookId = 1L;
    var book = new BookEntity();
    book.setId(bookId);
    book.setStock(5);
    when(bookRepositoryMock.findById(bookId)).thenReturn(Optional.of(book));

    bookService.incrementBookStock(bookId);

    verify(bookRepositoryMock).save(book);
    assertEquals(6, book.getStock(), "The stock should be incremented by 1");
  }

  @Test
  void testIncrementBookStock_BookNotFound() {
    var bookId = 1L;
    when(bookRepositoryMock.findById(bookId)).thenReturn(Optional.empty());

    var exception = assertThrows(BookNotFoundException.class, () -> {
      bookService.incrementBookStock(bookId);
    });

    assertEquals("Book not found with ID: " + bookId, exception.getMessage());
  }
}
