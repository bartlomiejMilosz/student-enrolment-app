package io.bartmilo.student.enrolment.app.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.bartmilo.student.enrolment.app.TestDataUtil;
import io.bartmilo.student.enrolment.app.domain.student.model.StudentDto;
import io.bartmilo.student.enrolment.app.domain.student.model.StudentEntity;
import io.bartmilo.student.enrolment.app.util.ModelMapper;
import io.bartmilo.student.enrolment.app.domain.student.service.StudentService;
import jakarta.persistence.EntityManager;
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
public class StudentControllerIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private StudentService studentService;

    @Autowired
    private ModelMapper<StudentEntity, StudentDto> studentModelMapper;

    @Autowired
    private EntityManager entityManager;

    @Test
    public void testThatCreateStudent_ReturnsHttpStatus201Created() throws Exception {
        var studentEntity = TestDataUtil.createSingleTestStudentEntity();
        var studentDto = studentModelMapper.mapFrom(studentEntity);
        String studentJson = objectMapper.writeValueAsString(studentDto);

        mockMvc.perform(
                post("/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(studentJson))
                .andExpect(status().isCreated());
    }

    @Test
    public void testThatCreateStudent_ReturnsSavedStudent() throws Exception {
        var studentEntity = TestDataUtil.createSingleTestStudentEntity();
        var studentDto = studentModelMapper.mapFrom(studentEntity);
        String studentJson = objectMapper.writeValueAsString(studentDto);

        mockMvc.perform(
                post("/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(studentJson))
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.firstName").value("Carol"))
                .andExpect(jsonPath("$.lastName").value("Raccoon"))
                .andExpect(jsonPath("$.email").value("carol.raccoon@gmail.com"))
                .andExpect(jsonPath("$.age").value(26)
                );
    }

    @Test
    public void testThatCreateStudent_SuccessfullyReturnsSavedStudentCardId() throws Exception {
        var studentEntity = TestDataUtil.createSingleTestStudentEntity();
        // trigger service to get student ID card
        studentService.save(studentEntity);
        studentEntity.getStudentIdCardEntity().setId(1L);

        var studentDto = studentModelMapper.mapFrom(studentEntity);
        String studentJson = objectMapper.writeValueAsString(studentDto);

        mockMvc.perform(
                        post("/students")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(studentJson))
                .andExpect(jsonPath("$.studentIdCard").exists())
                .andExpect(jsonPath("$.studentIdCard.status").value("ACTIVE"));
    }

    @Test
    public void testThatStudentList_ReturnsHttpStatus200Ok() throws Exception {
        mockMvc.perform(
                get("/students")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void testThatStudentList_ReturnsListOfStudents() throws Exception {
        var studentEntityList = TestDataUtil.createListOfTestStudentEntities();
        studentEntityList.forEach(studentService::save);

        mockMvc.perform(
                get("/students")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content[0].id").isNumber())
                .andExpect(jsonPath("$.content[0].firstName").value("Carol"))
                .andExpect(jsonPath("$.content[0].lastName").value("Raccoon"))
                .andExpect(jsonPath("$.content[0].email").value("carol.raccoon@gmail.com"))
                .andExpect(jsonPath("$.content[0].age").value(26)
                );
    }

    @Test
    public void testThatGetStudentReturnsHttpStatus200Ok_WhenStudentExists() throws Exception {
        var studentEntity = TestDataUtil.createSingleTestStudentEntity();
        studentService.save(studentEntity);

        mockMvc.perform(
                get("/students/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void testThatGetStudentReturnsHttpStatus404NotFound_WhenNoStudentExists() throws Exception {
        mockMvc.perform(
                get("/students/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testThatGetStudentReturnsStudent_WhenStudentExists() throws Exception {
        var studentEntity = TestDataUtil.createSingleTestStudentEntity();
        studentService.save(studentEntity);

        mockMvc.perform(
                get("/students/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.firstName").value("Carol"))
                .andExpect(jsonPath("$.lastName").value("Raccoon"))
                .andExpect(jsonPath("$.email").value("carol.raccoon@gmail.com"))
                .andExpect(jsonPath("$.age").value(26))
                .andExpect(jsonPath("$.studentIdCard").exists())
                .andExpect(jsonPath("$.studentIdCard.status").value("ACTIVE")
        );
    }

    @Test
    public void testThatFullUpdateStudent_ReturnsHttpStatus404NotFound_WhenNoStudentExists() throws Exception {
        var studentEntity = TestDataUtil.createSingleTestStudentEntity();
        var studentDto = studentModelMapper.mapFrom(studentEntity);
        String studentJson = objectMapper.writeValueAsString(studentDto);

        mockMvc.perform(
                put("/students/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(studentJson))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testThatFullUpdateStudent_ReturnsHttpStatus200Ok_WhenStudentExists() throws Exception {
        var studentEntity = TestDataUtil.createSingleTestStudentEntity();
        // trigger service to get student ID card
        studentService.save(studentEntity);
        studentEntity.getStudentIdCardEntity().setId(1L);
        var studentDto = studentModelMapper.mapFrom(studentEntity);
        String studentJson = objectMapper.writeValueAsString(studentDto);

        mockMvc.perform(
                put("/students/{id}", studentDto.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(studentJson))
                .andExpect(status().isOk());
    }

    @Test
    public void testThatFullUpdate_UpdatesExistingStudent() throws Exception {
        var studentEntity = TestDataUtil.createSingleTestStudentEntity();
        var savedStudent = studentService.save(studentEntity);

        var studentEntityToUpdate = TestDataUtil.createListOfTestStudentEntities().get(1);
        var studentDto = studentModelMapper.mapFrom(studentEntityToUpdate);
        studentDto.setId(savedStudent.getId());

        String studentJson = objectMapper.writeValueAsString(studentDto);

        mockMvc.perform(
                put("/students/{id}", savedStudent.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(studentJson))
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.firstName").value(studentDto.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(studentDto.getLastName()))
                .andExpect(jsonPath("$.email").value(studentDto.getEmail()))
                .andExpect(jsonPath("$.age").value(studentDto.getAge())
                );
    }

    @Test
    public void testThatPartialUpdateExistingStudent_ReturnsHttpStatus200Ok() throws Exception {
        var studentEntity = TestDataUtil.createSingleTestStudentEntity();
        var savedStudent = studentService.save(studentEntity);

        var studentDto = studentModelMapper.mapFrom(studentEntity);
        studentDto.setFirstName("UPDATED");
        String studentJson = objectMapper.writeValueAsString(studentDto);

        mockMvc.perform(
                patch("/students/{id}", savedStudent.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(studentJson))
                .andExpect(status().isOk());
    }

    @Test
    public void testThatPartialUpdateExistingStudent_ReturnsUpdatedStudent() throws Exception {
        var studentEntity = TestDataUtil.createSingleTestStudentEntity();
        var savedStudent = studentService.save(studentEntity);

        var studentDto = studentModelMapper.mapFrom(studentEntity);
        studentDto.setFirstName("UPDATED");
        String studentJson = objectMapper.writeValueAsString(studentDto);

        mockMvc.perform(
                        patch("/students/{id}", savedStudent.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(studentJson))
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.firstName").value("UPDATED"))
                .andExpect(jsonPath("$.lastName").value(studentEntity.getLastName()))
                .andExpect(jsonPath("$.email").value(studentEntity.getEmail()))
                .andExpect(jsonPath("$.age").value(studentEntity.getAge())
                );
    }

    @Test
    public void testThatDeleteStudent_ReturnsHttpStatus204_ForNonExistingStudent() throws Exception {
        mockMvc.perform(
                delete("/students/999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testThatDeleteStudent_ReturnsHttpStatus204_ForExistingStudent() throws Exception {
        var studentEntity = TestDataUtil.createSingleTestStudentEntity();
        var savedStudent = studentService.save(studentEntity);

        mockMvc.perform(
                delete("/students/{id}", savedStudent.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }
}
