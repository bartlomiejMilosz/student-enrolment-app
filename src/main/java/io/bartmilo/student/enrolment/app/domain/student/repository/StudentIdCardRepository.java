package io.bartmilo.student.enrolment.app.domain.student.repository;

import io.bartmilo.student.enrolment.app.domain.student.model.StudentIdCardEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentIdCardRepository extends
        CrudRepository<StudentIdCardEntity, Long>,
        PagingAndSortingRepository<StudentIdCardEntity, Long> {
}
