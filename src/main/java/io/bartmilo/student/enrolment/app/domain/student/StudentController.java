package io.bartmilo.student.enrolment.app.domain.student;

import io.bartmilo.student.enrolment.app.domain.student.model.StudentDto;
import io.bartmilo.student.enrolment.app.domain.student.model.StudentEntity;
import io.bartmilo.student.enrolment.app.util.DomainMapper;
import io.bartmilo.student.enrolment.app.domain.student.service.StudentService;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/students")
public class StudentController {

    private static final Logger LOGGER = LoggerFactory.getLogger(StudentController.class);
    private final StudentService studentService;
    private final DomainMapper<StudentEntity, StudentDto> studentModelMapper;

    @Autowired
    public StudentController(StudentService studentService, DomainMapper<StudentEntity, StudentDto> studentModelMapper) {
        this.studentService = studentService;
        this.studentModelMapper = studentModelMapper;
    }

    /**
     * Saves a new student
     * @param studentDto   The student to save.
     * @return             The saved student.
     */
    @PostMapping
    public ResponseEntity<StudentDto> saveStudent(@RequestBody StudentDto studentDto) {
        LOGGER.info("Mapping student {} to dt  o", studentDto);
        var studentEntity = studentModelMapper.mapTo(studentDto);

        LOGGER.info(
                "Saving new student with name: {}, surname {}, email {} and age {} to database",
                studentEntity.getFirstName(),
                studentEntity.getLastName(),
                studentEntity.getEmail(),
                studentEntity.getAge()
        );

        var savedStudentEntity = studentService.save(studentEntity);
        var savedStudentDto = studentModelMapper.mapFrom(savedStudentEntity);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedStudentDto.getId())
                .toUri();

        return ResponseEntity
                .created(location)
                .body(savedStudentDto);
    }

    /**
     * Returns the student with the specified ID.
     * @param id    The ID od the student to retrieve.
     * @return      The student with the specified ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<StudentDto> findStudentById(@PathVariable Long id) {
        LOGGER.info("Attempting to find student with ID: {}", id);

        var studentEntity = studentService.findById(id)
                .orElseThrow(() -> {
                    LOGGER.error("No student found with ID: {}", id);
                    return new EntityNotFoundException("Cannot find student with provided ID: " + id);
                });

        var studentDto = studentModelMapper.mapFrom(studentEntity);
        LOGGER.info("Successfully found and mapped student with ID: {}", id);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(studentDto);
    }

    /**
     * Returns all students in the database.
     * @return All students in the database.
     */
    @GetMapping
    public ResponseEntity<Page<StudentDto>> findAllStudents(@PageableDefault(size = 10) Pageable pageable) {
        LOGGER.info(
                "Request to fetch all students with page: {} and size: {}",
                pageable.getPageNumber(),
                pageable.getPageSize()
        );

        var studentEntityPage = studentService.findAll(pageable);
        var studentDtoPage = studentEntityPage.map(studentModelMapper::mapFrom);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(studentDtoPage);
    }

    /**
     * Updates the student with the specified ID.
     * @param id           The ID of the student to update.
     * @param studentDto   The student to update.
     * @return             The updated student.
     */
    @PutMapping("/{id}")
    // TODO: consider changing it to PATCH method
    public ResponseEntity<StudentDto> fullUpdateStudent(
            @PathVariable Long id,
            @RequestBody StudentDto studentDto
    ) {
        LOGGER.info("Attempting to update student with ID: {}", id);

        if (studentService.isExists(id)) {
            LOGGER.error("No student found with ID: {}", id);
            return ResponseEntity
                    .notFound()
                    .build();
        }

        LOGGER.info("Confirmed student exists, updating student with ID: {}", id);
        studentDto.setId(id);

        var studentEntity = studentModelMapper.mapTo(studentDto);
        var savedStudentEntity = studentService.save(studentEntity);

        LOGGER.info("Student with ID: {} updated successfully", id);
        var savedStudentDto = studentModelMapper.mapFrom(savedStudentEntity);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .build()
                .toUri();

        return ResponseEntity
                .ok()
                .location(location)
                .body(savedStudentDto);
    }

    /**
     * Updates the student with the specified ID.
     * @param id           The ID of the student to update.
     * @param studentDto   The student to update.
     * @return             The updated student.
     */
    @PatchMapping("/{id}")
    public ResponseEntity<StudentDto> partialUpdateStudent(
            @PathVariable("id") Long id,
            @RequestBody StudentDto studentDto
    ) {
        LOGGER.info("Attempting to update student with ID: {}", id);

        if (studentService.isExists(id)) {
            LOGGER.error("No student found with ID: {}", id);
            return ResponseEntity
                    .notFound()
                    .build();
        }

        LOGGER.info("Confirmed student exists, updating student with ID: {}", id);
        studentDto.setId(id);

        var studentEntity = studentModelMapper.mapTo(studentDto);
        var savedStudentEntity = studentService.partialUpdate(id, studentEntity);

        LOGGER.info("Student with ID: {} updated successfully", id);
        var savedStudentDto = studentModelMapper.mapFrom(savedStudentEntity);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .build()
                .toUri();

        return ResponseEntity
                .ok()
                .location(location)
                .body(savedStudentDto);
    }


    /**
     * Deletes the student with the specified ID.
     * @param id    The ID of the student to delete.
     * @return      A ResponseEntity with OK 200 status if the deletion was successful
     *              else Not Found 404 if a product with the specified ID is not found or
     *              Internal Service Error 500 if an error occurs during deletion - served by ControllerAdvise
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudent(@PathVariable Long id) {
        LOGGER.info("Deleting product with ID {}", id);
        studentService.delete(id);

        LOGGER.info("Student with ID: {} deleted successfully", id);
        return ResponseEntity
                .noContent()
                .build();
    }
}
