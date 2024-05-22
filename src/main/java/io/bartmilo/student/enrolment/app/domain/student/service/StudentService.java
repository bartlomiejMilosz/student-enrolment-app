package io.bartmilo.student.enrolment.app.domain.student.service;

import io.bartmilo.student.enrolment.app.domain.student.model.StudentDto;
import io.bartmilo.student.enrolment.app.domain.student.model.StudentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface StudentService {

  /**
   * Saves the specified student to the database.
   *
   * @param studentDto The student to save to database.
   * @return The saved student.
   */
  StudentDto save(StudentDto studentDto);

  /**
   * Returns all students in the database.
   *
   * @return All students in the database.
   */
  Page<StudentDto> findAll(Pageable pageable);

  /**
   * Returns the student with the specified id.
   *
   * @param id ID of the student to retrieve.
   * @return The requested student if found.
   */
  StudentDto findById(Long id);

  /**
   * Returns boolean of student in the database with the specified ID. Checks whether the student
   * exists in the database.
   *
   * @param id ID of the student to check.
   * @return The requested student existence.
   */
  boolean exists(Long id);

  /**
   * Updates the specified student, identified by given ID.
   *
   * @param id The ID of the student to update.
   * @param studentDto The student to update.
   * @return The updated student.
   */
  StudentDto partialUpdate(Long id, StudentDto studentDto);

  /**
   * Deletes the student with the specified ID.
   *
   * @param id The ID of the student to delete.
   */
  void delete(Long id);
}
