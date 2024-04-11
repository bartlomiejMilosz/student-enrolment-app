package io.bartmilo.student.enrolment.app.mapper.impl;

import io.bartmilo.student.enrolment.app.domain.dto.BookDto;
import io.bartmilo.student.enrolment.app.domain.entity.BookEntity;
import io.bartmilo.student.enrolment.app.mapper.Mapper;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class BookMapperImpl implements Mapper<BookEntity, BookDto> {
    private final ModelMapper modelMapper;

    public BookMapperImpl(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public BookEntity mapTo(BookDto bookDto) {
        return modelMapper.map(bookDto, BookEntity.class);
    }

    @Override
    public BookDto mapFrom(BookEntity bookEntity) {
        return modelMapper.map(bookEntity, BookDto.class);
    }
}
