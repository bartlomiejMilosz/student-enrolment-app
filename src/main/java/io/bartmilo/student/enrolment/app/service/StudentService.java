package io.bartmilo.student.enrolment.app.service;

import io.bartmilo.student.enrolment.app.domain.entity.StudentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface StudentService {
    /**
     * Saves the specified student to the database.
     *
     * @param studentEntity The student to save to database.
     * @return The saved student.
     */
    StudentEntity save(StudentEntity studentEntity);

    /**
     * Returns all students in the database.
     *
     * @return All students in the database.
     */
    Page<StudentEntity> findAll(Pageable pageable);

    /**
     * Returns the student with the specified id.
     *
     * @param id ID of the student to retrieve.
     * @return The requested student if found.
     */
    Optional<StudentEntity> findById(Long id);

    /**
     * Updates the specified student, identified by given ID.
     *
     * @param id            The ID of the student to update.
     * @param studentEntity The student to update.
     * @return The updated student.
     */
    StudentEntity partialUpdate(Long id, StudentEntity studentEntity);

    /**
     * Deletes the student with the specified ID.
     *
     * @param id The ID of the student to delete.
     */
    void delete(Long id);
}
