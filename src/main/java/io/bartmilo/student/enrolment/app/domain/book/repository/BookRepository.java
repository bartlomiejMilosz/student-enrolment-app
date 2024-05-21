package io.bartmilo.student.enrolment.app.domain.book.repository;

import io.bartmilo.student.enrolment.app.domain.book.model.BookEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends
        CrudRepository<BookEntity, Long>,
        PagingAndSortingRepository<BookEntity, Long> {
}
