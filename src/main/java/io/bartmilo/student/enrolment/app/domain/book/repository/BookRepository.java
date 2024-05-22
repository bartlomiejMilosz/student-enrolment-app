package io.bartmilo.student.enrolment.app.domain.book.repository;

import io.bartmilo.student.enrolment.app.domain.book.model.BookEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<BookEntity, Long> {}
