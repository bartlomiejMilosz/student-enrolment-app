package io.bartmilo.student.enrolment.app.mapper.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.bartmilo.student.enrolment.app.domain.dto.RentalDto;
import io.bartmilo.student.enrolment.app.domain.entity.RentalEntity;
import io.bartmilo.student.enrolment.app.mapper.Mapper;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class RentalMapper implements Mapper<RentalEntity, RentalDto> {
    private final ModelMapper modelMapper;

    public RentalMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public RentalEntity mapTo(RentalDto rentalDto) {
        return modelMapper.map(rentalDto, RentalEntity.class);
    }

    @Override
    public RentalDto mapFrom(RentalEntity rentalEntity) {
        return modelMapper.map(rentalEntity, RentalDto.class);
    }
}
