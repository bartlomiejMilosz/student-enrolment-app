package io.bartmilo.student.enrolment.app.domain.student.mapper;

import io.bartmilo.student.enrolment.app.domain.student.model.StudentDto;
import io.bartmilo.student.enrolment.app.domain.student.model.StudentEntity;
import io.bartmilo.student.enrolment.app.domain.student.model.StudentRequest;
import io.bartmilo.student.enrolment.app.domain.student.model.StudentResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(
    componentModel = "spring",
    uses = {StudentIdCardMapper.class},
    unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface StudentMapper {

  StudentMapper INSTANCE = Mappers.getMapper(StudentMapper.class);

  @Mapping(source = "studentIdCardEntity", target = "studentIdCardDto")
  StudentDto convertEntityToDto(StudentEntity entity);

  StudentEntity convertDtoToEntity(StudentDto dto);

  @Mapping(target = "id", ignore = true)
  StudentDto convertRequestToDto(StudentRequest request);

  @Mapping(source = "studentIdCardDto", target = "studentIdCardResponse")
  StudentResponse convertDtoToResponse(StudentDto dto);
}
