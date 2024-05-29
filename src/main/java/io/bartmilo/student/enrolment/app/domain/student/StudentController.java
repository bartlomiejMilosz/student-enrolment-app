package io.bartmilo.student.enrolment.app.domain.student;

import io.bartmilo.student.enrolment.app.domain.student.exception.StudentNotFoundException;
import io.bartmilo.student.enrolment.app.domain.student.mapper.StudentMapper;
import io.bartmilo.student.enrolment.app.domain.student.model.*;
import io.bartmilo.student.enrolment.app.domain.student.service.StudentService;
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
  private final StudentMapper studentMapper;

  public StudentController(StudentService studentService, StudentMapper studentMapper) {
    this.studentService = studentService;
    this.studentMapper = studentMapper;
  }

  @PostMapping
  public ResponseEntity<StudentResponse> createStudent(@RequestBody StudentRequest studentRequest) {
    LOGGER.info("Request to create student: {}", studentRequest);
    var studentDto = studentMapper.convertRequestToDto(studentRequest);
    var savedStudentDto = studentService.save(studentDto);
    var location =
        ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(savedStudentDto.id())
            .toUri();
    LOGGER.info("Student created successfully: {}", savedStudentDto);
    var studentResponse = studentMapper.convertDtoToResponse(savedStudentDto);
    return ResponseEntity.created(location).body(studentResponse);
  }

  @GetMapping("/{id}")
  public ResponseEntity<StudentResponse> getStudentById(@PathVariable Long id) {
    LOGGER.info("Request to get student by ID: {}", id);
    var studentDto = studentService.findById(id);
    var studentResponse = studentMapper.convertDtoToResponse(studentDto);
    LOGGER.info("Student retrieved successfully: {}", studentDto);
    return ResponseEntity.ok(studentResponse);
  }

  @GetMapping
  public ResponseEntity<Page<StudentResponse>> getAllStudents(
      @PageableDefault(size = 10) Pageable pageable) {
    LOGGER.info("Request to get all students");
    var studentDtoPage = studentService.findAll(pageable);
    var studentResponsePage = studentDtoPage.map(studentMapper::convertDtoToResponse);
    LOGGER.info("Students retrieved with pagination");
    return ResponseEntity.ok(studentResponsePage);
  }

  @PutMapping("/{id}")
  public ResponseEntity<StudentResponse> updateStudent(
      @PathVariable Long id, @RequestBody StudentDto studentDto) {
    LOGGER.info("Request to update student with ID: {}", id);
    checkIfTheStudentExists(id);
    var updatedStudentDto = studentService.save(studentDto);
    var studentResponse = studentMapper.convertDtoToResponse(updatedStudentDto);
    LOGGER.info("Student updated successfully: {}", updatedStudentDto);
    return ResponseEntity.ok(studentResponse);
  }

  @PatchMapping("/{id}")
  public ResponseEntity<StudentResponse> partialUpdateStudent(
      @PathVariable Long id, @RequestBody StudentDto studentDto) {
    LOGGER.info("Request to partially update student with ID: {}", id);
    checkIfTheStudentExists(id);
    var updatedStudentDto = studentService.partialUpdate(id, studentDto);
    var studentResponse = studentMapper.convertDtoToResponse(updatedStudentDto);
    LOGGER.info("Student partially updated successfully: {}", updatedStudentDto);
    return ResponseEntity.ok(studentResponse);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteStudent(@PathVariable Long id) {
    LOGGER.info("Request to delete student with ID: {}", id);
    checkIfTheStudentExists(id);
    studentService.delete(id);
    LOGGER.info("Student deleted successfully with ID: {}", id);
    return ResponseEntity.noContent().build();
  }

  private void checkIfTheStudentExists(Long id) {
    if (!studentService.exists(id)) {
      LOGGER.error("Attempted to update a non-existent student with ID: {}", id);
      throw new StudentNotFoundException("Cannot update non-existing student");
    }
  }
}
