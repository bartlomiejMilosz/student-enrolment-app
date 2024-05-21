package io.bartmilo.student.enrolment.app.domain.student.repository;

import io.bartmilo.student.enrolment.app.domain.student.model.StudentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<StudentEntity, Long> {
    @Query("SELECT s FROM Student s WHERE s.email = ?1")
    Optional<StudentEntity> findStudentByEmail(String email);

    @Query("SELECT s FROM Student s WHERE s.firstName = ?1 AND s.age = ?2")
    List<StudentEntity> findStudentsByFirstNameAndAgeEquals(String firstName, Integer age);

    @Query(
            value = "SELECT * FROM student WHERE first_name = ?1 AND age >= ?2",
            nativeQuery = true
    )
    List<StudentEntity> findStudentsByFirstNameAndAgeGreaterOrEqualNative(String firstName, Integer age);

    @Query(
            value = "SELECT * FROM student WHERE first_name = :firstName AND age >= :age",
            nativeQuery = true
    )
    List<StudentEntity> findStudentsByFirstNameAndAgeGreaterOrEqualNative_NamedParameters(
            @Param("firstName") String firstName,
            @Param("age") Integer age
    );

    @Query("SELECT s FROM Student s WHERE s.lastName LIKE ?1")
    List<StudentEntity> findByLastNameStartingWith(String prefix);
}
