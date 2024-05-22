package io.bartmilo.student.enrolment.app.domain.rental.repository;

import io.bartmilo.student.enrolment.app.domain.rental.model.RentalEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RentalRepository extends JpaRepository<RentalEntity, Long> {}
