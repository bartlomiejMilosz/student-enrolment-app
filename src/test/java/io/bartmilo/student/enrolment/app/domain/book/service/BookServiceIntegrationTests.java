package io.bartmilo.student.enrolment.app.domain.book.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.bartmilo.student.enrolment.app.TestDataUtil;
import io.bartmilo.student.enrolment.app.domain.book.model.BookDto;
import io.bartmilo.student.enrolment.app.domain.book.model.BookEntity;
import io.bartmilo.student.enrolment.app.util.DomainMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
    private final DomainMapper domainMapper;

    public BookServiceIntegrationTests(BookService bookService, DomainMapper domainMapper) {
        this.bookService = bookService;
        this.domainMapper = domainMapper;
    }

  @Test
  void whenBookIsSaved_ThanCanBeRecalled() {
    var bookEntity = TestDataUtil.createSingleTestBookEntity();
    var bookDto = domainMapper.convertEntityToDto(bookEntity, BookDto.class);
    bookService.save(bookDto);

    var result = bookService.findById(bookDto.id());

    assertAll(() -> assertThat(result).isNotNull(), () -> assertThat(result).isEqualTo(bookDto));
  }

  @Test
  void whenListOfBooksIsSaved_ThanCanBeRecalled() {
    var bookList = TestDataUtil.createListOfTestBookEntities();
    bookList.forEach(bookService::save);

    int page = 0;
    int size = 3;
    var pageable = PageRequest.of(page, size);

    var result = bookService.findAll(pageable);

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
        });
  }

  @Test
  void whenFindById_ThenReturnBook() {
    var book = TestDataUtil.createSingleTestBookEntity();
    bookService.save(book);

    var foundBook = bookService.findById(book.getId());

    assertThat(foundBook).isPresent().contains(book);
  }

  @Test
  void whenCheckExistence_ThenConfirm() {
    var book = TestDataUtil.createSingleTestBookEntity();
    bookService.save(book);

    boolean exists = !bookService.isExists(book.getId());

    assertTrue(exists);
  }

  @Test
  void whenPartialUpdate_ThenUpdateFields() {
    var book = TestDataUtil.createSingleTestBookEntity();
    book = bookService.save(book);
    var updatedInfo = new BookEntity();
    updatedInfo.setTitle("UpdatedTitle");

    var updatedBook = bookService.partialUpdate(book.getId(), updatedInfo);

    assertThat(updatedBook.getTitle()).isEqualTo("UpdatedTitle");
  }

  @Test
  void whenDelete_ThenBookIsNotPresent() {
    var book = TestDataUtil.createSingleTestBookEntity();
    book = bookService.save(book);

    bookService.delete(book.getId());

    var result = bookService.findById(book.getId());
    assertThat(result).isEmpty();
  }
}
