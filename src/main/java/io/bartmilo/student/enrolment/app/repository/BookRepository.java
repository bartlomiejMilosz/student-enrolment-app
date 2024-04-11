package io.bartmilo.student.enrolment.app.repository;

import io.bartmilo.student.enrolment.app.domain.entity.BookEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends
        CrudRepository<BookEntity, Long>,
        PagingAndSortingRepository<BookEntity, Long> {
}
