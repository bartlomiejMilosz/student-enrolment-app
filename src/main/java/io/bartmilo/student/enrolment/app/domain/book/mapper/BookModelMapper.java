package io.bartmilo.student.enrolment.app.domain.book.mapper;

import io.bartmilo.student.enrolment.app.domain.book.model.BookDto;
import io.bartmilo.student.enrolment.app.domain.book.model.BookEntity;
import io.bartmilo.student.enrolment.app.util.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class BookModelMapper implements ModelMapper<BookEntity, BookDto> {
    private final org.modelmapper.ModelMapper modelMapper;

    public BookModelMapper(org.modelmapper.ModelMapper modelMapper) {
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
