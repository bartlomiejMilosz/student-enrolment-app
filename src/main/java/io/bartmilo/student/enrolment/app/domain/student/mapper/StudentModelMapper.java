package io.bartmilo.student.enrolment.app.domain.student.mapper;

import io.bartmilo.student.enrolment.app.domain.student.model.StudentDto;
import io.bartmilo.student.enrolment.app.domain.student.model.StudentEntity;
import io.bartmilo.student.enrolment.app.util.DomainMapper;
import org.springframework.stereotype.Component;

@Component
public class StudentModelMapper implements DomainMapper<StudentEntity, StudentDto> {
    private final org.modelmapper.ModelMapper modelMapper;

    public StudentModelMapper(org.modelmapper.ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public StudentEntity mapTo(StudentDto studentDto) {
        return modelMapper.map(studentDto, StudentEntity.class);
    }

    @Override
    public StudentDto mapFrom(StudentEntity studentEntity) {
        return modelMapper.map(studentEntity, StudentDto.class);
    }
}
