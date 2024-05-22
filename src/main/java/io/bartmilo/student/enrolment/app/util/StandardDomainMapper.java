package io.bartmilo.student.enrolment.app.util;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class StandardDomainMapper implements DomainMapper {
    private final ModelMapper modelMapper;

    public StandardDomainMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public <D, E> D convertEntityToDto(E entity, Class<D> dtoClass) {
        return modelMapper.map(entity, dtoClass);
    }

    @Override
    public <D, E> E convertDtoToEntity(D dto, Class<E> entityClass) {
        return modelMapper.map(dto, entityClass);
    }

    @Override
    public <R, D> R convertDtoToResponse(D dto, Class<R> responseClass) {
        return modelMapper.map(dto, responseClass);
    }

    @Override
    public <R, D> D convertRequestToDto(R request, Class<D> dtoClass) {
        return modelMapper.map(request, dtoClass);
    }
}
