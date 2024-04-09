package io.bartmilo.student.enrolment.app.service;

import io.bartmilo.student.enrolment.app.domain.StudentEntity;

import java.util.List;
import java.util.Optional;

public interface StudentService {
    StudentEntity save(StudentEntity student);

    List<StudentEntity> findAll();

    Optional<StudentEntity> findById(Long id);

    StudentEntity partialUpdate(Long id, StudentEntity authorEntity);

    void delete(Long id);
}
