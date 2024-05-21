package io.bartmilo.student.enrolment.app.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.bartmilo.student.enrolment.app.TestDataUtil;
import io.bartmilo.student.enrolment.app.domain.rental.model.RentalDto;
import io.bartmilo.student.enrolment.app.domain.rental.model.RentalEntity;
import io.bartmilo.student.enrolment.app.util.ModelMapper;
import io.bartmilo.student.enrolment.app.domain.book.repository.BookRepository;
import io.bartmilo.student.enrolment.app.domain.rental.repository.RentalRepository;
import io.bartmilo.student.enrolment.app.domain.rental.service.RentalService;
import io.bartmilo.student.enrolment.app.domain.student.service.StudentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static java.time.LocalDateTime.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureMockMvc
public class RentalControllerIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RentalService rentalService;

    @Autowired
    private RentalRepository rentalRepository;

    @Autowired
    private StudentService studentService;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private ModelMapper<RentalEntity, RentalDto> rentalModelMapper;

    @Test
    public void testRentBook_ReturnsCreatedAndRentalDetails() throws Exception {
        // Prepare
        var book = TestDataUtil.createSingleTestBookEntity();
        bookRepository.save(book);

        var student = TestDataUtil.createSingleTestStudentEntity();
        studentService.save(student);

        var rentalDto = RentalDto.builder()
                .bookId(1L)
                .studentId(1L)
                .dueDate(now().plusDays(30))
                .build();

        String rentalJson = objectMapper.writeValueAsString(rentalDto);

        // Perform and Assert
        mockMvc.perform(
                post("/rentals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(rentalJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.studentId").value(rentalDto.getStudentId()))
                .andExpect(jsonPath("$.bookId").value(rentalDto.getBookId()))
                .andExpect(jsonPath("$.dueDate").exists()); // Check the due date was set correctly
    }

    @Test
    public void testReturnBook_ReturnsOkAndUpdatedRentalDetails() throws Exception {
        // Prepare - create a rental
        var rental = RentalEntity.builder()
                .id(1L)
                .bookEntity(TestDataUtil.createSingleTestBookEntity())
                .rentedAt(now().minusDays(10))
                .dueDate(now().plusDays(30))
                .build();
        rentalRepository.save(rental);

        // Perform and Assert
        mockMvc.perform(
                        put("/rentals/{id}", rental.getId())
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.returnedAt").exists()); // Confirm the returned date is now set
    }
}
