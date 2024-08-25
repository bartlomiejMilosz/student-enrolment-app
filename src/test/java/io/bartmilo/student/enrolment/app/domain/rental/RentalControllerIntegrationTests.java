package io.bartmilo.student.enrolment.app.domain.rental;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.bartmilo.student.enrolment.app.TestDataUtil;
import io.bartmilo.student.enrolment.app.domain.book.service.BookService;
import io.bartmilo.student.enrolment.app.domain.rental.model.RentalRequest;
import io.bartmilo.student.enrolment.app.domain.rental.model.RentalResponse;
import io.bartmilo.student.enrolment.app.domain.rental.service.RentalService;
import io.bartmilo.student.enrolment.app.domain.student.service.StudentService;
import java.time.LocalDateTime;
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
class RentalControllerIntegrationTests {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @Autowired private RentalService rentalService;

  @Autowired private StudentService studentService;

  @Autowired private BookService bookService;

  @Test
  void testRentBook_ReturnsCreatedAndRentalDetails() throws Exception {
    var studentDto = TestDataUtil.createSingleTestStudentDto();
    var savedStudentDto = studentService.save(studentDto);
    var bookDto = TestDataUtil.createSingleTestBookDto();
    var savedBookDto = bookService.save(bookDto);

    var rentalRequest =
        RentalRequest.builder()
            .bookId(savedBookDto.getId())
            .studentId(savedStudentDto.id())
            .dueDate(LocalDateTime.now().plusDays(30))
            .build();

    var rentalRequestJson = objectMapper.writeValueAsString(rentalRequest);

    mockMvc
        .perform(
            post("/rentals").contentType(MediaType.APPLICATION_JSON).content(rentalRequestJson))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.studentId").value(rentalRequest.studentId()))
        .andExpect(jsonPath("$.bookId").value(rentalRequest.bookId()))
        .andExpect(jsonPath("$.dueDate").exists());
  }

  @Test
  void testReturnBook_ReturnsOkAndUpdatedRentalDetails() throws Exception {
    var studentDto = TestDataUtil.createSingleTestStudentDto();
    var savedStudentDto = studentService.save(studentDto);
    var bookDto = TestDataUtil.createSingleTestBookDto();
    var savedBookDto = bookService.save(bookDto);

    var rentalRequest =
        RentalRequest.builder()
            .bookId(savedBookDto.getId())
            .studentId(savedStudentDto.id())
            .dueDate(LocalDateTime.now().plusDays(30))
            .build();

    var rentalRequestJson = objectMapper.writeValueAsString(rentalRequest);

    var rentalResponseJson =
        mockMvc
            .perform(
                post("/rentals").contentType(MediaType.APPLICATION_JSON).content(rentalRequestJson))
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getContentAsString();

    var rentalResponse = objectMapper.readValue(rentalResponseJson, RentalResponse.class);

    mockMvc
        .perform(put("/rentals/{id}", rentalResponse.id()).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.returnedAt").exists());
  }
}
