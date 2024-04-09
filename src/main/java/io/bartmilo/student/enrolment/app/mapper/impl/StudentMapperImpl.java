package io.bartmilo.student.enrolment.app.mapper.impl;

import io.bartmilo.student.enrolment.app.domain.dto.StudentDto;
import io.bartmilo.student.enrolment.app.domain.entity.StudentEntity;
import io.bartmilo.student.enrolment.app.mapper.Mapper;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class StudentMapperImpl implements Mapper<StudentEntity, StudentDto> {
    private final ModelMapper modelMapper;

    public StudentMapperImpl(ModelMapper modelMapper) {
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
