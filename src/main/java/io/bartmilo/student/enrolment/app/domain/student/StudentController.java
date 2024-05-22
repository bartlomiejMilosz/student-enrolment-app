package io.bartmilo.student.enrolment.app.domain.student;

import io.bartmilo.student.enrolment.app.domain.student.model.StudentDto;
import io.bartmilo.student.enrolment.app.domain.student.model.StudentResponse;
import io.bartmilo.student.enrolment.app.domain.student.service.StudentService;
import io.bartmilo.student.enrolment.app.util.DomainMapper;
import java.net.URI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/students")
public class StudentController {

  private static final Logger LOGGER = LoggerFactory.getLogger(StudentController.class);
  private final StudentService studentService;
  private final DomainMapper domainMapper;

  public StudentController(StudentService studentService, DomainMapper domainMapper) {
    this.studentService = studentService;
    this.domainMapper = domainMapper;
  }

  @PostMapping
  public ResponseEntity<StudentResponse> createStudent(@RequestBody StudentDto studentDto) {
    LOGGER.info("Request to create student: {}", studentDto);
    var savedStudentDto = studentService.save(studentDto);
    var studentResponse = domainMapper.convertDtoToResponse(savedStudentDto, StudentResponse.class);

    URI location =
        ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(savedStudentDto.id())
            .toUri();

    LOGGER.info("Student created successfully: {}", savedStudentDto);
    return ResponseEntity.created(location).body(studentResponse);
  }

  @GetMapping("/{id}")
  public ResponseEntity<StudentResponse> getStudentById(@PathVariable Long id) {
    LOGGER.info("Request to get student by ID: {}", id);
    var studentDto = studentService.findById(id);
    var studentResponse = domainMapper.convertDtoToResponse(studentDto, StudentResponse.class);
    LOGGER.info("Student retrieved successfully: {}", studentDto);
    return ResponseEntity.ok(studentResponse);
  }

  @GetMapping
  public ResponseEntity<Page<StudentResponse>> getAllStudents(
      @PageableDefault(size = 10) Pageable pageable) {
    LOGGER.info("Request to get all students");
    var studentDtoPage = studentService.findAll(pageable);
    var studentResponsePage =
        studentDtoPage.map(dto -> domainMapper.convertDtoToResponse(dto, StudentResponse.class));
    LOGGER.info("Students retrieved with pagination");
    return ResponseEntity.ok(studentResponsePage);
  }

  @PutMapping("/{id}")
  public ResponseEntity<StudentResponse> updateStudent(
      @PathVariable Long id, @RequestBody StudentDto studentDto) {
    LOGGER.info("Request to update student with ID: {}", id);
    if (!studentService.exists(id)) {
      LOGGER.error("Attempted to update a non-existent student with ID: {}", id);
      return ResponseEntity.notFound().build();
    }
    var updatedStudentDto = studentService.save(studentDto);
    var studentResponse =
        domainMapper.convertDtoToResponse(updatedStudentDto, StudentResponse.class);
    LOGGER.info("Student updated successfully: {}", updatedStudentDto);
    return ResponseEntity.ok(studentResponse);
  }

  @PatchMapping("/{id}")
  public ResponseEntity<StudentResponse> partialUpdateStudent(
      @PathVariable Long id, @RequestBody StudentDto studentDto) {
    LOGGER.info("Request to partially update student with ID: {}", id);
    if (!studentService.exists(id)) {
      LOGGER.error("Attempted to update a non-existent student with ID: {}", id);
      return ResponseEntity.notFound().build();
    }
    var updatedStudentDto = studentService.partialUpdate(id, studentDto);
    var studentResponse =
        domainMapper.convertDtoToResponse(updatedStudentDto, StudentResponse.class);
    LOGGER.info("Student partially updated successfully: {}", updatedStudentDto);
    return ResponseEntity.ok(studentResponse);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteStudent(@PathVariable Long id) {
    LOGGER.info("Request to delete student with ID: {}", id);
    if (!studentService.exists(id)) {
      LOGGER.error("Attempted to delete a non-existent student with ID: {}", id);
      return ResponseEntity.notFound().build();
    }
    studentService.delete(id);
    LOGGER.info("Student deleted successfully with ID: {}", id);
    return ResponseEntity.noContent().build();
  }
}
