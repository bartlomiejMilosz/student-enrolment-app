package io.bartmilo.student.enrolment.app.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.bartmilo.student.enrolment.app.TestDataUtil;
import io.bartmilo.student.enrolment.app.domain.dto.BookDto;
import io.bartmilo.student.enrolment.app.domain.entity.BookEntity;
import io.bartmilo.student.enrolment.app.mapper.Mapper;
import io.bartmilo.student.enrolment.app.service.BookService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureMockMvc
public class BookControllerIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BookService bookService;

    @Autowired
    private Mapper<BookEntity, BookDto> bookMapper;

    @Test
    public void testThatCreateBook_ReturnsHttpStatus201Created() throws Exception {
        var bookEntity = TestDataUtil.createSingleTestBookEntity();
        var bookDto = bookMapper.mapFrom(bookEntity);
        String bookJson = objectMapper.writeValueAsString(bookDto);

        mockMvc.perform(
                        post("/books")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(bookJson))
                .andExpect(status().isCreated());
    }

    @Test
    public void testThatCreateBook_ReturnsSavedBook() throws Exception {
        var bookEntity = TestDataUtil.createSingleTestBookEntity();
        var bookDto = bookMapper.mapFrom(bookEntity);
        bookDto.setId(1L);
        String bookJson = objectMapper.writeValueAsString(bookDto);

        mockMvc.perform(
                        post("/books")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(bookJson))
                .andExpect(jsonPath("$.id").value(bookDto.getId()))
                .andExpect(jsonPath("$.bookAuthor").value(bookDto.getBookAuthor()))
                .andExpect(jsonPath("$.title").value(bookDto.getTitle()))
                .andExpect(jsonPath("$.isbn").value(bookDto.getIsbn()))
                .andExpect(jsonPath("$.stock").value(bookDto.getStock())
                );
    }

    @Test
    public void testThatBookList_ReturnsHttpStatus200Ok() throws Exception {
        mockMvc.perform(
                        get("/books")
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void testThatBookList_ReturnsListOfBooks() throws Exception {
        var bookEntityList = TestDataUtil.createListOfTestBookEntities();
        bookEntityList.forEach(bookService::save);

        mockMvc.perform(
                        get("/books")
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content[0].id").isNumber())
                .andExpect(jsonPath("$.content[0].bookAuthor").value("George Orwell"))
                .andExpect(jsonPath("$.content[0].title").value("1984"))
                .andExpect(jsonPath("$.content[0].isbn").value("978-0451524935"))
                .andExpect(jsonPath("$.content[0].stock").value(10)
                );
    }

    @Test
    public void testThatGetBookReturnsHttpStatus200Ok_WhenBookExists() throws Exception {
        var bookEntity = TestDataUtil.createSingleTestBookEntity();
        bookService.save(bookEntity);

        mockMvc.perform(
                        get("/books/{id}", bookEntity.getId())
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void testThatGetBookReturnsHttpStatus404NotFound_WhenNoBookExists() throws Exception {
        mockMvc.perform(
                        get("/books/1")
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testThatGetBookReturnsBook_WhenBookExists() throws Exception {
        var bookEntity = TestDataUtil.createSingleTestBookEntity();
        bookEntity.setId(1L);
        bookService.save(bookEntity);
        var bookDto = bookMapper.mapFrom(bookEntity);

        mockMvc.perform(
                        get("/books/{id}", bookEntity.getId())
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(bookDto.getId()))
                .andExpect(jsonPath("$.bookAuthor").value("George Orwell"))
                .andExpect(jsonPath("$.title").value("1984"))
                .andExpect(jsonPath("$.isbn").value("978-0451524935"))
                .andExpect(jsonPath("$.stock").value(10)
                );
    }

    @Test
    public void testThatFullUpdateBook_ReturnsHttpStatus404NotFound_WhenNoBookExists() throws Exception {
        var bookEntity = TestDataUtil.createSingleTestBookEntity();
        var bookDto = bookMapper.mapFrom(bookEntity);
        String bookJson = objectMapper.writeValueAsString(bookDto);

        mockMvc.perform(
                        put("/books/99")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(bookJson))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testThatFullUpdateBook_ReturnsHttpStatus200Ok_WhenBookExists() throws Exception {
        var bookEntity = TestDataUtil.createSingleTestBookEntity();
        // trigger service to get student ID card
        bookService.save(bookEntity);
        var bookDto = bookMapper.mapFrom(bookEntity);
        String bookJson = objectMapper.writeValueAsString(bookDto);

        mockMvc.perform(
                        put("/books/{id}", bookDto.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(bookJson))
                .andExpect(status().isOk());
    }

    @Test
    public void testThatFullUpdate_UpdatesExistingBook() throws Exception {
        var bookEntity = TestDataUtil.createSingleTestBookEntity();
        var savedBook = bookService.save(bookEntity);

        var bookEntityToUpdate = TestDataUtil.createListOfTestBookEntities().get(1);
        var bookDto = bookMapper.mapFrom(bookEntityToUpdate);
        bookDto.setId(savedBook.getId());

        String bookJson = objectMapper.writeValueAsString(bookDto);

        mockMvc.perform(
                        put("/books/{id}", savedBook.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(bookJson))
                .andExpect(jsonPath("$.id").value(bookDto.getId()))
                .andExpect(jsonPath("$.bookAuthor").value(bookDto.getBookAuthor()))
                .andExpect(jsonPath("$.title").value(bookDto.getTitle()))
                .andExpect(jsonPath("$.isbn").value(bookDto.getIsbn()))
                .andExpect(jsonPath("$.stock").value(bookDto.getStock())
                );
    }

    @Test
    public void testThatDeleteBook_ReturnsHttpStatus404NotFound_ForNonExistingBook() throws Exception {
        mockMvc.perform(
                        delete("/books/999")
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testThatDeleteBook_ReturnsHttpStatus204_ForExistingBook() throws Exception {
        var bookEntity = TestDataUtil.createSingleTestBookEntity();
        var savedBook = bookService.save(bookEntity);

        mockMvc.perform(
                        delete("/books/{id}", savedBook.getId())
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    /* OLDER TESTS */

    @Test
    public void testCreateBook_ReturnsCreated() throws Exception {
        BookDto bookDto = bookMapper.mapFrom(TestDataUtil.createSingleTestBookEntity());
        String json = objectMapper.writeValueAsString(bookDto);

        mockMvc.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title").value(bookDto.getTitle()));
    }

    @Test
    public void testGetBookById_ReturnsBookDetails() throws Exception {
        BookEntity book = TestDataUtil.createSingleTestBookEntity();
        BookEntity savedBook = bookService.save(book);

        mockMvc.perform(get("/books/{id}", savedBook.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedBook.getId()))
                .andExpect(jsonPath("$.title").value(book.getTitle()));
    }

    @Test
    public void testGetAllBooks_ReturnsBooks() throws Exception {
        TestDataUtil.createListOfTestBookEntities().forEach(bookService::save);

        mockMvc.perform(get("/books")
                        .param("page", "0")
                        .param("size", "5")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").isNumber())
                .andExpect(jsonPath("$.totalPages").value(1));
    }

    @Test
    public void testUpdateBook_UpdatesCorrectly() throws Exception {
        var book = TestDataUtil.createSingleTestBookEntity();
        var savedBook = bookService.save(book);

        var bookDto = bookMapper.mapFrom(book);
        bookDto.setTitle("UPDATED");
        String bookJson = objectMapper.writeValueAsString(bookDto);

        mockMvc.perform(put("/books/{id}", savedBook.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bookJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("UPDATED"));
    }

    @Test
    public void testDeleteBook_ReturnsNoContent() throws Exception {
        var book = TestDataUtil.createSingleTestBookEntity();
        var savedBook = bookService.save(book);

        mockMvc.perform(delete("/books/{id}", savedBook.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testDeleteNonExistingBook_ReturnsNotFound() throws Exception {
        mockMvc.perform(delete("/books/{id}", 999)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
