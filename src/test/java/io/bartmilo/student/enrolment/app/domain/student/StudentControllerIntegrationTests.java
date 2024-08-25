package io.bartmilo.student.enrolment.app.domain.student;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.bartmilo.student.enrolment.app.TestDataUtil;
import io.bartmilo.student.enrolment.app.domain.student.model.StudentDto;
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

@SpringBootTest
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureMockMvc
class StudentControllerIntegrationTests {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;
  @Autowired private StudentService studentService;

  @Test
  void testCreateStudent_ReturnsHttpStatus201Created() throws Exception {
    var studentDto = TestDataUtil.createSingleTestStudentDto();
    var studentJson = objectMapper.writeValueAsString(studentDto);

    mockMvc
        .perform(post("/students").contentType(MediaType.APPLICATION_JSON).content(studentJson))
        .andExpect(status().isCreated());
  }

  @Test
  void testCreateStudent_ReturnsSavedStudentDetails() throws Exception {
    var studentDto = TestDataUtil.createSingleTestStudentDto();
    var studentJson = objectMapper.writeValueAsString(studentDto);

    mockMvc
        .perform(post("/students").contentType(MediaType.APPLICATION_JSON).content(studentJson))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").exists())
        .andExpect(jsonPath("$.firstName").value(studentDto.firstName()))
        .andExpect(jsonPath("$.lastName").value(studentDto.lastName()))
        .andExpect(jsonPath("$.email").value(studentDto.email()))
        .andExpect(jsonPath("$.age").value(studentDto.age()));
  }

  @Test
  void testGetAllStudents_ReturnsHttpStatus200Ok() throws Exception {
    mockMvc
        .perform(get("/students").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());
  }

  @Test
  void testGetAllStudents_ReturnsListOfStudents() throws Exception {
    TestDataUtil.createListOfTestStudentDto().forEach(studentService::save);

    mockMvc
        .perform(get("/students").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content").isArray())
        .andExpect(jsonPath("$.content[0].id").isNumber());
  }

  @Test
  void testGetStudentById_ReturnsHttpStatus200Ok() throws Exception {
    var studentDto = TestDataUtil.createSingleTestStudentDto();
    var savedStudent = studentService.save(studentDto);

    mockMvc
        .perform(get("/students/{id}", savedStudent.id()).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());
  }

  @Test
  void testGetStudentById_ReturnsHttpStatus404NotFound() throws Exception {
    mockMvc
        .perform(get("/students/{id}", 1).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }

  @Test
  void testDeleteStudent_ReturnsHttpStatus204NoContent() throws Exception {
    var studentDto = TestDataUtil.createSingleTestStudentDto();
    var savedStudent = studentService.save(studentDto);

    mockMvc
        .perform(
            delete("/students/{id}", savedStudent.id()).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNoContent());
  }

  @Test
  void testDeleteNonExistingStudent_ReturnsHttpStatus404NotFound() throws Exception {
    mockMvc
        .perform(delete("/students/{id}", 999).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }
}
