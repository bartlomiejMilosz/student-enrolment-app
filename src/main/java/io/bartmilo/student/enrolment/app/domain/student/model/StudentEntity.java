package io.bartmilo.student.enrolment.app.domain.student.model;

import io.bartmilo.student.enrolment.app.domain.rental.model.RentalEntity;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import lombok.*;

@Getter
@Setter
@ToString(exclude = {"studentIdCardEntity", "rentalEntityList"})
@EqualsAndHashCode(exclude = {"studentIdCardEntity", "rentalEntityList"})
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity(name = "Student")
@Table(
    name = "student",
    uniqueConstraints = {@UniqueConstraint(name = "student_email_unique", columnNames = "email")})
public class StudentEntity {
  @Id
  @SequenceGenerator(
      name = "student_sequence",
      sequenceName = "student_sequence",
      allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "student_sequence")
  @Column(name = "id", updatable = false)
  private Long id;

  @Column(name = "first_name", nullable = false, columnDefinition = "TEXT")
  private String firstName;

  @Column(name = "last_name", nullable = false, columnDefinition = "TEXT")
  private String lastName;

  @Column(name = "email", nullable = false, columnDefinition = "TEXT")
  private String email;

  @Column(name = "age", nullable = false)
  private Integer age;

  /* RELATIONS */

  @OneToOne(
      mappedBy = "studentEntity",
      orphanRemoval = true,
      cascade = CascadeType.MERGE)
  private StudentIdCardEntity studentIdCardEntity;

  @OneToMany(mappedBy = "studentEntity", orphanRemoval = false)
  private List<RentalEntity> rentalEntityList = new ArrayList<>();
}
