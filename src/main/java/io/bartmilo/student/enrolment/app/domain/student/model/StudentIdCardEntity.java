package io.bartmilo.student.enrolment.app.domain.student.model;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@ToString(exclude = "studentEntity")
@EqualsAndHashCode(exclude = "studentEntity")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity(name = "StudentIdCard")
@Table(
    name = "student_id_card",
    uniqueConstraints = {
      @UniqueConstraint(name = "student_id_card_student_id_unique", columnNames = "card_number")
    })
public class StudentIdCardEntity {

  @Id
  @SequenceGenerator(
      name = "student_card_id_sequence",
      sequenceName = "student_card_id_sequence",
      allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "student_card_id_sequence")
  @Column(name = "id", updatable = false)
  private Long id;

  @Column(name = "card_number", nullable = false, length = 15)
  private String cardNumber;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false)
  private IdCardStatus status;

  /* RELATIONS */

  @OneToOne(fetch = FetchType.EAGER)
  @JoinColumn(
      name = "student_id",
      referencedColumnName = "id",
      foreignKey = @ForeignKey(name = "student_id_card_student_id_fk"))
  private StudentEntity studentEntity;
}
