package io.bartmilo.student.enrolment.app.domain.book.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.bartmilo.student.enrolment.app.TestDataUtil;
import io.bartmilo.student.enrolment.app.domain.book.exception.BookNotFoundException;
import io.bartmilo.student.enrolment.app.domain.book.mapper.BookMapper;
import io.bartmilo.student.enrolment.app.domain.book.model.BookDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class BookServiceIntegrationTests {

  private final BookService bookService;
  private final BookMapper bookMapper;

  @Autowired
  public BookServiceIntegrationTests(BookService bookService, BookMapper bookMapper) {
    this.bookService = bookService;
    this.bookMapper = bookMapper;
  }

  @Test
  void whenBookIsSaved_ThanCanBeRecalled() {
    var bookEntity = TestDataUtil.createSingleTestBookEntity();
    var bookDto = bookMapper.convertEntityToDto(bookEntity);
    var savedBookDto = bookService.save(bookDto);

    var result = bookService.findById(savedBookDto.getId());
    if (result == null) throw new NullPointerException();

    assertAll(
        () -> assertThat(result).isNotNull(),
        () -> assertThat(result.getId()).isEqualTo(savedBookDto.getId()),
        () -> assertThat(result.getBookAuthor()).isEqualTo(savedBookDto.getBookAuthor()),
        () -> assertThat(result.getTitle()).isEqualTo(savedBookDto.getTitle()),
        () -> assertThat(result.getCreatedAt()).isEqualTo(savedBookDto.getCreatedAt()),
        () -> assertThat(result.getIsbn()).isEqualTo(savedBookDto.getIsbn()),
        () -> assertThat(result.getStock()).isEqualTo(savedBookDto.getStock()));
  }

  @Test
  void whenListOfBooksIsSaved_ThanCanBeRecalled() {
    var bookEntityList = TestDataUtil.createListOfTestBookEntity();
    var bookDtoList = bookEntityList.stream().map(bookMapper::convertEntityToDto).toList();
    var savedBookDtoList = bookDtoList.stream().map(bookService::save).toList();
    var page = 0;
    var size = 3;
    var pageable = PageRequest.of(page, size);

    var result = bookService.findAll(pageable);

    assertAll(
        () -> assertThat(result.getContent()).hasSize(size),
        () -> assertThat(result.getNumber()).isEqualTo(page),
        () -> assertThat(result.getSize()).isEqualTo(size),
        () -> assertThat(result.getTotalElements()).isEqualTo(savedBookDtoList.size()),
        () ->
            assertThat(result.getContent().stream().map(BookDto::getId).toList())
                .containsExactlyInAnyOrderElementsOf(
                    savedBookDtoList.stream().map(BookDto::getId).toList()),
        () ->
            assertThat(result.getContent().stream().map(BookDto::getTitle).toList())
                .containsExactlyInAnyOrderElementsOf(
                    savedBookDtoList.stream().map(BookDto::getTitle).toList()));
  }

  @Test
  void whenFindById_ThenReturnBook() {
    var bookEntity = TestDataUtil.createSingleTestBookEntity();
    var bookDto = bookMapper.convertEntityToDto(bookEntity);
    var savedBookDto = bookService.save(bookDto);

    var foundBookDto = bookService.findById(savedBookDto.getId());

    assertThat(foundBookDto).isEqualTo(savedBookDto);
  }

  @Test
  void whenCheckExistence_ThenConfirm() {
    var bookEntity = TestDataUtil.createSingleTestBookEntity();
    var bookDto = bookMapper.convertEntityToDto(bookEntity);
    var savedBookDto = bookService.save(bookDto);

    boolean exists = bookService.isExists(savedBookDto.getId());

    assertTrue(exists);
  }

  @Test
  void whenPartialUpdate_ThenUpdateFields() {
    var bookEntity = TestDataUtil.createSingleTestBookEntity();
    var bookDto = bookMapper.convertEntityToDto(bookEntity);
    var savedBookDto = bookService.save(bookDto);
    var updatedBookDtoInfo = new BookDto();
    updatedBookDtoInfo.setTitle("UPDATED TITLE");

    var updatedBookDto = bookService.partialUpdate(savedBookDto.getId(), updatedBookDtoInfo);

    assertThat(updatedBookDto.getTitle()).isEqualTo("UPDATED TITLE");
  }

  @Test
  void whenDelete_ThenBookIsNotPresent() {
    var bookEntity = TestDataUtil.createSingleTestBookEntity();
    var bookDto = bookMapper.convertEntityToDto(bookEntity);
    var savedBookDto = bookService.save(bookDto);

    bookService.delete(savedBookDto.getId());

    Executable executable = () -> bookService.findById(savedBookDto.getId());
    assertThrows(BookNotFoundException.class, executable);
  }
}
