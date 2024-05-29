package io.bartmilo.student.enrolment.app.domain.book;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.bartmilo.student.enrolment.app.TestDataUtil;
import io.bartmilo.student.enrolment.app.domain.book.mapper.BookMapper;
import io.bartmilo.student.enrolment.app.domain.book.service.BookService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureMockMvc
class BookControllerIntegrationTests {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;
  @Autowired private BookMapper bookMapper;
  @Autowired private BookService bookService;

  @Test
  void testThatCreateBook_ReturnsHttpStatus201Created() throws Exception {
    var bookEntity = TestDataUtil.createSingleTestBookEntity();
    var bookDto = bookMapper.convertEntityToDto(bookEntity);
    var bookJson = objectMapper.writeValueAsString(bookDto);

    mockMvc
        .perform(post("/books").contentType(MediaType.APPLICATION_JSON).content(bookJson))
        .andExpect(status().isCreated());
  }

  @Test
  void testThatCreateBook_ReturnsSavedBook() throws Exception {
    var bookEntity = TestDataUtil.createSingleTestBookEntity();
    var bookDto = bookMapper.convertEntityToDto(bookEntity);
    bookDto.setId(1L);
    var bookJson = objectMapper.writeValueAsString(bookDto);

    mockMvc
        .perform(post("/books").contentType(MediaType.APPLICATION_JSON).content(bookJson))
        .andExpect(jsonPath("$.id").value(bookDto.getId()))
        .andExpect(jsonPath("$.bookAuthor").value(bookDto.getBookAuthor()))
        .andExpect(jsonPath("$.title").value(bookDto.getTitle()))
        .andExpect(jsonPath("$.isbn").value(bookDto.getIsbn()))
        .andExpect(jsonPath("$.stock").value(bookDto.getStock()));
  }

  @Test
  void testThatBookList_ReturnsHttpStatus200Ok() throws Exception {
    mockMvc
        .perform(get("/books").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());
  }

  @Test
  void testThatBookList_ReturnsListOfBooks() throws Exception {
    var bookEntityList = TestDataUtil.createListOfTestBookEntity();
    var bookDtoList = bookEntityList.stream().map(bookMapper::convertEntityToDto).toList();
    bookDtoList.forEach(bookService::save);

    mockMvc
        .perform(get("/books").contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.content[0].id").isNumber())
        .andExpect(jsonPath("$.content[0].bookAuthor").value("George Orwell"))
        .andExpect(jsonPath("$.content[0].title").value("1984"))
        .andExpect(jsonPath("$.content[0].isbn").value("978-0451524935"))
        .andExpect(jsonPath("$.content[0].stock").value(10));
  }

  @Test
  void testThatGetBookReturnsHttpStatus200Ok_WhenBookExists() throws Exception {
    var bookEntity = TestDataUtil.createSingleTestBookEntity();
    var bookDto = bookMapper.convertEntityToDto(bookEntity);
    var savedBookDto = bookService.save(bookDto);

    mockMvc
        .perform(get("/books/{id}", savedBookDto.getId()).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());
  }

  @Test
  void testThatGetBookReturnsHttpStatus404NotFound_WhenNoBookExists() throws Exception {
    mockMvc
        .perform(get("/books/1").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }

  @Test
  void testThatGetBookReturnsBook_WhenBookExists() throws Exception {
    var bookEntity = TestDataUtil.createSingleTestBookEntity();
    var bookDto = bookMapper.convertEntityToDto(bookEntity);
    var savedBookDto = bookService.save(bookDto);

    mockMvc
        .perform(get("/books/{id}", savedBookDto.getId()).contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id").value(savedBookDto.getId()))
        .andExpect(jsonPath("$.bookAuthor").value("George Orwell"))
        .andExpect(jsonPath("$.title").value("1984"))
        .andExpect(jsonPath("$.isbn").value("978-0451524935"))
        .andExpect(jsonPath("$.stock").value(10));
  }

  @Test
  void testThatFullUpdateBook_ReturnsHttpStatus404NotFound_WhenNoBookExists() throws Exception {
    var bookEntity = TestDataUtil.createSingleTestBookEntity();
    var bookDto = bookMapper.convertEntityToDto(bookEntity);
    var bookJson = objectMapper.writeValueAsString(bookDto);

    mockMvc
        .perform(put("/books/99").contentType(MediaType.APPLICATION_JSON).content(bookJson))
        .andExpect(status().isNotFound());
  }

  @Test
  void testThatFullUpdateBook_ReturnsHttpStatus200Ok_WhenBookExists() throws Exception {
    var bookEntity = TestDataUtil.createSingleTestBookEntity();
    var bookDto = bookMapper.convertEntityToDto(bookEntity);
    var savedBookDto = bookService.save(bookDto);

    savedBookDto.setTitle("UPDATED TITLE");
    var bookJson = objectMapper.writeValueAsString(savedBookDto);

    mockMvc
        .perform(
            put("/books/{id}", savedBookDto.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(bookJson))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.title").value("UPDATED TITLE"));
  }

  @Test
  void testThatFullUpdate_UpdatesExistingBook() throws Exception {
    var initialBookDto = TestDataUtil.createSingleTestBookDto();
    var savedBookDto = bookService.save(initialBookDto);
    var updatedBookDto = TestDataUtil.createListOfTestBookDto().get(1);
    updatedBookDto.setId(savedBookDto.getId());

    var bookJson = objectMapper.writeValueAsString(updatedBookDto);

    mockMvc
        .perform(
            put("/books/{id}", savedBookDto.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(bookJson))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(updatedBookDto.getId()))
        .andExpect(jsonPath("$.bookAuthor").value(updatedBookDto.getBookAuthor()))
        .andExpect(jsonPath("$.title").value(updatedBookDto.getTitle()))
        .andExpect(jsonPath("$.isbn").value(updatedBookDto.getIsbn()))
        .andExpect(jsonPath("$.stock").value(updatedBookDto.getStock()));
  }

  @Test
  void testThatDeleteBook_ReturnsHttpStatus404NotFound_ForNonExistingBook() throws Exception {
    mockMvc
        .perform(delete("/books/999").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }

  @Test
  void testThatDeleteBook_ReturnsHttpStatus204_ForExistingBook() throws Exception {
    var bookEntity = TestDataUtil.createSingleTestBookEntity();
    var bookDto = bookMapper.convertEntityToDto(bookEntity);
    var savedBookDto = bookService.save(bookDto);

    mockMvc
        .perform(
            delete("/books/{id}", savedBookDto.getId()).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNoContent());
  }

  @Test
  void testCreateBook_ReturnsCreated() throws Exception {
    var bookDto = TestDataUtil.createSingleTestBookDto();
    var json = objectMapper.writeValueAsString(bookDto);

    mockMvc
        .perform(post("/books").contentType(MediaType.APPLICATION_JSON).content(json))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").exists())
        .andExpect(jsonPath("$.title").value(bookDto.getTitle()));
  }

  @Test
  void testGetBookById_ReturnsBookDetails() throws Exception {
    var bookEntity = TestDataUtil.createSingleTestBookEntity();
    var bookDto = bookMapper.convertEntityToDto(bookEntity);
    var savedBookDto = bookService.save(bookDto);

    mockMvc
        .perform(get("/books/{id}", savedBookDto.getId()).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(savedBookDto.getId()))
        .andExpect(jsonPath("$.title").value(bookEntity.getTitle()));
  }

  @Test
  void testGetAllBooks_ReturnsBooks() throws Exception {
    TestDataUtil.createListOfTestBookDto().forEach(bookService::save);

    mockMvc
        .perform(
            get("/books")
                .param("page", "0")
                .param("size", "5")
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content").isArray())
        .andExpect(jsonPath("$.content[0].id").isNumber())
        .andExpect(jsonPath("$.totalPages").value(1));
  }

  @Test
  void testUpdateBook_UpdatesCorrectly() throws Exception {
    var bookEntity = TestDataUtil.createSingleTestBookEntity();
    var bookDto = bookMapper.convertEntityToDto(bookEntity);
    var savedBookDto = bookService.save(bookDto);

    bookDto.setTitle("UPDATED");
    var bookJson = objectMapper.writeValueAsString(bookDto);

    mockMvc
        .perform(
            put("/books/{id}", savedBookDto.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(bookJson))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.title").value("UPDATED"));
  }

  @Test
  void testDeleteBook_ReturnsNoContent() throws Exception {
    var bookEntity = TestDataUtil.createSingleTestBookEntity();
    var bookDto = bookMapper.convertEntityToDto(bookEntity);
    var savedBookDto = bookService.save(bookDto);

    mockMvc
        .perform(
            delete("/books/{id}", savedBookDto.getId()).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNoContent());
  }

  @Test
  void testDeleteNonExistingBook_ReturnsNotFound() throws Exception {
    mockMvc
        .perform(delete("/books/{id}", 999).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }
}
