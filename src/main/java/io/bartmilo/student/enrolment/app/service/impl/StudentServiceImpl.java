package io.bartmilo.student.enrolment.app.service.impl;

import io.bartmilo.student.enrolment.app.domain.entity.StudentEntity;
import io.bartmilo.student.enrolment.app.repository.StudentRepository;
import io.bartmilo.student.enrolment.app.service.StudentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Spliterator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Service
public class StudentServiceImpl implements StudentService {

    private static final Logger LOGGER = LoggerFactory.getLogger(StudentServiceImpl.class);

    private final StudentRepository studentRepository;

    @Autowired
    public StudentServiceImpl(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @Override
    @Transactional
    public StudentEntity save(StudentEntity student) {
        LOGGER.info("Save student to the database: {}", student);
        return studentRepository.save(student);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<StudentEntity> findAll(Pageable pageable) {
        LOGGER.info("Fetching all students with pagination");
        return studentRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<StudentEntity> findById(Long id) {
        LOGGER.info("Find student with id: {}", id);
        return studentRepository.findById(id);
    }

    @Override
    @Transactional
    public StudentEntity partialUpdate(Long id, StudentEntity studentEntity) {
        LOGGER.info("Make sure the student you want to update has specified id: {}", id);
        studentEntity.setId(id);

        LOGGER.info("Update student: {}, with id: {}", studentEntity, id);
        return studentRepository.findById(id).map(existingAuthor -> {
            Optional.ofNullable(studentEntity.getFirstName())
                    .ifPresent(existingAuthor::setFirstName);
            Optional.ofNullable(studentEntity.getLastName())
                    .ifPresent(existingAuthor::setLastName);
            Optional.ofNullable(studentEntity.getEmail())
                    .ifPresent(existingAuthor::setEmail);
            Optional.ofNullable(studentEntity.getAge())
                    .ifPresent(existingAuthor::setAge);
            return studentRepository.save(existingAuthor);
        }).orElseThrow(() -> new RuntimeException("Student does not exist"));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        LOGGER.info("Delete student with id: {}", id);
        studentRepository.deleteById(id);
    }
}
