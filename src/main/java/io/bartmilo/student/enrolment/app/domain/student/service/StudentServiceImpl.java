package io.bartmilo.student.enrolment.app.domain.student.service;

import io.bartmilo.student.enrolment.app.domain.student.model.StudentEntity;
import io.bartmilo.student.enrolment.app.domain.student.repository.StudentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class StudentServiceImpl implements StudentService {

    private static final Logger LOGGER = LoggerFactory.getLogger(StudentServiceImpl.class);

    private final StudentRepository studentRepository;
    private final StudentIdCardService studentIdCardService;

    @Autowired
    public StudentServiceImpl(
            StudentRepository studentRepository,
            StudentIdCardService studentIdCardService
    ) {
        this.studentRepository = studentRepository;
        this.studentIdCardService = studentIdCardService;
    }

    @Override
    @Transactional
    public StudentEntity save(StudentEntity student) {
        var idCard = studentIdCardService.generateStudentIdCard(student);
        student.setStudentIdCardEntity(idCard);
        LOGGER.info("Save student to the database: {}", student);

        // TODO: keep it but consider
        if (student.getId() != null && findById(student.getId()).isPresent()) {
            return studentRepository.saveAndFlush(student); // Ensure merge behavior
        } else {
            return studentRepository.save(student); // New entity
        }
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
    @Transactional(readOnly = true)
    public boolean isExists(Long id) {
        LOGGER.info("Check whether the student exists with id: {}", id);
        return !studentRepository.existsById(id);
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