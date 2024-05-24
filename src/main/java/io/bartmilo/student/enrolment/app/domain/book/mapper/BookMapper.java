package io.bartmilo.student.enrolment.app.domain.book.mapper;

import io.bartmilo.student.enrolment.app.domain.book.model.BookDto;
import io.bartmilo.student.enrolment.app.domain.book.model.BookEntity;
import io.bartmilo.student.enrolment.app.domain.book.model.BookRequest;
import io.bartmilo.student.enrolment.app.domain.book.model.BookResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BookMapper {

  BookMapper INSTANCE = Mappers.getMapper(BookMapper.class);

  BookDto convertEntityToDto(BookEntity entity);

  BookEntity convertDtoToEntity(BookDto dto);

  @Mapping(target = "id", ignore = true)
  BookDto convertRequestToDto(BookRequest request);

  BookResponse convertDtoToResponse(BookDto dto);
}
