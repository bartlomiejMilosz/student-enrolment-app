package io.bartmilo.student.enrolment.app.domain.student.service;

import io.bartmilo.student.enrolment.app.domain.student.exception.StudentNotFoundException;
import io.bartmilo.student.enrolment.app.domain.student.model.StudentDto;
import io.bartmilo.student.enrolment.app.domain.student.model.StudentEntity;
import io.bartmilo.student.enrolment.app.domain.student.model.StudentIdCardEntity;
import io.bartmilo.student.enrolment.app.domain.student.repository.StudentRepository;
import io.bartmilo.student.enrolment.app.util.DomainMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StudentServiceImpl implements StudentService {
    private static final Logger LOGGER = LoggerFactory.getLogger(StudentServiceImpl.class);
    private final StudentRepository studentRepository;
    private final StudentIdCardService studentIdCardService;
    private final DomainMapper domainMapper;

    public StudentServiceImpl(StudentRepository studentRepository, StudentIdCardService studentIdCardService, DomainMapper domainMapper) {
        this.studentRepository = studentRepository;
        this.studentIdCardService = studentIdCardService;
        this.domainMapper = domainMapper;
    }

    @Override
    @Transactional
    public StudentDto save(StudentDto studentDto) {
        LOGGER.info("Saving student to the database: {}", studentDto);
        var studentIdCardDto = studentIdCardService.generateStudentIdCard(studentDto);
        var studentIdCardEntity = domainMapper.convertDtoToEntity(studentIdCardDto, StudentIdCardEntity.class);
        var studentEntity = domainMapper.convertDtoToEntity(studentDto, StudentEntity.class);
        studentEntity.setStudentIdCardEntity(studentIdCardEntity);

        var savedStudentEntity = studentRepository.save(studentEntity);
        return domainMapper.convertEntityToDto(savedStudentEntity, StudentDto.class);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<StudentDto> findAll(Pageable pageable) {
        LOGGER.info("Fetching all students with pagination");
        var studentEntityPage = studentRepository.findAll(pageable);
        return studentEntityPage.map(entity -> domainMapper.convertEntityToDto(entity, StudentDto.class));
    }

    @Override
    @Transactional(readOnly = true)
    public StudentDto findById(Long id) {
        LOGGER.info("Finding student with ID: {}", id);
        return studentRepository
                .findById(id)
                .map(studentEntity -> domainMapper.convertEntityToDto(studentEntity, StudentDto.class))
                .orElseThrow(() -> new StudentNotFoundException("Student not found with ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean exists(Long id) {
        LOGGER.info("Checking whether the student exists with ID: {}", id);
        return studentRepository.existsById(id);
    }

    @Override
    @Transactional
    public StudentDto partialUpdate(Long id, StudentDto studentDto) {
        LOGGER.info("Updating student with ID: {}", id);
        return studentRepository
                .findById(id)
                .map(existingStudent -> updateExistingStudent(studentDto, existingStudent))
                .orElseThrow(() -> new StudentNotFoundException("Student not found with ID: " + id));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        LOGGER.info("Deleting student with ID: {}", id);
        studentRepository.deleteById(id);
    }

    private StudentDto updateExistingStudent(StudentDto studentDto, StudentEntity existingStudent) {
        LOGGER.info("Found student to update: {}", existingStudent);

        if (studentDto.firstName() != null) {
            existingStudent.setFirstName(studentDto.firstName());
        }
        if (studentDto.lastName() != null) {
            existingStudent.setLastName(studentDto.lastName());
        }
        if (studentDto.email() != null) {
            existingStudent.setEmail(studentDto.email());
        }
        if (studentDto.age() != null) {
            existingStudent.setAge(studentDto.age());
        }

        var updatedStudent = studentRepository.save(existingStudent);
        LOGGER.info("Updated student: {}", updatedStudent);
        return domainMapper.convertEntityToDto(updatedStudent, StudentDto.class);
    }
}
