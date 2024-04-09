package io.bartmilo.student.enrolment.app.domain.entity;

import jakarta.persistence.*;
import lombok.*;


@Data
// @ToString - TODO
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity(name = "Student")
@Table(
        name = "student",
        uniqueConstraints = {
                @UniqueConstraint(name = "student_email_unique", columnNames = "email")
        }
)
public class StudentEntity {
        @Id
        @SequenceGenerator(
                name = "student_sequence",
                sequenceName = "student_sequence",
                allocationSize = 1
        )
        @GeneratedValue(
                strategy = GenerationType.SEQUENCE,
                generator = "student_sequence"
        )
        @Column(
                name = "id",
                updatable = false
        )
        private Long id;

        @Column(
                name = "first_name",
                nullable = false,
                columnDefinition = "TEXT"
        )
        private String firstName;

        @Column(
                name = "last_name",
                nullable = false,
                columnDefinition = "TEXT"
        )
        private String lastName;

        @Column(
                name = "email",
                nullable = false,
                columnDefinition = "TEXT"
        )
        private String email;

        @Column(
                name = "age",
                nullable = false
        )
        private Integer age;
}