package io.bartmilo.student.enrolment.app.domain.rental.repository;

import io.bartmilo.student.enrolment.app.domain.rental.model.RentalEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RentalRepository extends
        CrudRepository<RentalEntity, Long>,
        PagingAndSortingRepository<RentalEntity, Long> {
}
