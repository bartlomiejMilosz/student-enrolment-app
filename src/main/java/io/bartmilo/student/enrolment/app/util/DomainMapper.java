package io.bartmilo.student.enrolment.app.util;

public interface DomainMapper {
  <D, E> D convertEntityToDto(E entity, Class<D> dtoClass);

  <D, E> E convertDtoToEntity(D dto, Class<E> entityClass);

  <R, D> R convertDtoToResponse(D dto, Class<R> responseClass);

  <R, D> D convertRequestToDto(R request, Class<D> dtoClass);
}

