package io.bartmilo.student.enrolment.app.domain.rental.mapper;

import io.bartmilo.student.enrolment.app.domain.rental.model.RentalDto;
import io.bartmilo.student.enrolment.app.domain.rental.model.RentalEntity;
import io.bartmilo.student.enrolment.app.domain.rental.model.RentalRequest;
import io.bartmilo.student.enrolment.app.domain.rental.model.RentalResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RentalMapper {

  RentalMapper INSTANCE = Mappers.getMapper(RentalMapper.class);

  @Mapping(source = "bookEntity.id", target = "bookId")
  @Mapping(source = "studentEntity.id", target = "studentId")
  RentalDto convertEntityToDto(RentalEntity entity);

  @Mapping(source = "bookId", target = "bookEntity.id")
  @Mapping(source = "studentId", target = "studentEntity.id")
  RentalEntity convertDtoToEntity(RentalDto dto);

  @Mapping(target = "id", ignore = true)
  RentalDto convertRequestToDto(RentalRequest request);

  RentalResponse convertDtoToResponse(RentalDto dto);
}
