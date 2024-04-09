package io.bartmilo.student.enrolment.app.service.impl;

import io.bartmilo.student.enrolment.app.domain.entity.StudentEntity;
import io.bartmilo.student.enrolment.app.repository.StudentRepository;
import io.bartmilo.student.enrolment.app.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Spliterator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Service
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;

    @Autowired
    public StudentServiceImpl(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @Override
    @Transactional
    public StudentEntity save(StudentEntity student) {
        return studentRepository.save(student);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentEntity> findAll() {
        Iterable<StudentEntity> allAuthors = studentRepository.findAll();
        Spliterator<StudentEntity> spliterator = allAuthors.spliterator();
        Stream<StudentEntity> stream = StreamSupport.stream(spliterator, false);
        return stream.toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<StudentEntity> findById(Long id) {
        return studentRepository.findById(id);
    }

    @Override
    @Transactional
    public StudentEntity partialUpdate(Long id, StudentEntity studentEntity) {
        studentEntity.setId(id);

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
        studentRepository.deleteById(id);
    }
}
