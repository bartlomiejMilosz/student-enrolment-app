package io.bartmilo.student.enrolment.app.domain.student.repository;

import io.bartmilo.student.enrolment.app.domain.student.model.StudentIdCardEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentIdCardRepository extends JpaRepository<StudentIdCardEntity, Long> {}
