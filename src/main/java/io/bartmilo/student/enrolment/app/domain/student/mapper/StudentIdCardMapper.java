package io.bartmilo.student.enrolment.app.domain.student.mapper;

import io.bartmilo.student.enrolment.app.domain.student.model.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface StudentIdCardMapper {

  StudentIdCardMapper INSTANCE = Mappers.getMapper(StudentIdCardMapper.class);

  @Mapping(source = "studentEntity.id", target = "studentId")
  StudentIdCardDto convertEntityToDto(StudentIdCardEntity entity);

  StudentIdCardEntity convertDtoToEntity(StudentIdCardDto dto);

  @Mapping(target = "id", ignore = true)
  StudentIdCardDto convertRequestToDto(StudentIdCardRequest request);

  @Mapping(target = "studentId", source = "studentId")
  StudentIdCardResponse convertDtoToResponse(StudentIdCardDto dto);
}
