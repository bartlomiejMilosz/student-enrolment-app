package io.bartmilo.student.enrolment.app.domain.rental.mapper;

import io.bartmilo.student.enrolment.app.domain.rental.model.RentalDto;
import io.bartmilo.student.enrolment.app.domain.rental.model.RentalEntity;
import io.bartmilo.student.enrolment.app.util.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class RentalModelMapper implements ModelMapper<RentalEntity, RentalDto> {
    private final org.modelmapper.ModelMapper modelMapper;

    public RentalModelMapper(org.modelmapper.ModelMapper modelMapper) {
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
