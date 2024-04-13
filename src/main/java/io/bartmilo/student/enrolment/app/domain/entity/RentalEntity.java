package io.bartmilo.student.enrolment.app.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@ToString(
        exclude = {
                "bookEntity",
                "studentEntity"
        }
)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "rental")
public class RentalEntity {
    @Id
    @SequenceGenerator(
            name = "rental_sequence",
            sequenceName = "rental_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "rental_sequence"
    )
    @Column(
            name = "id",
            updatable = false
    )
    private Long id;

    @Column(
            name = "rented_at",
            nullable = true, // by default is set to .now()
            columnDefinition = "TIMESTAMP WITHOUT TIME ZONE"
    )
    private LocalDateTime rentedAt;

    @Column(
            name = "due_date",
            nullable = false, // should specify due date by hand
            columnDefinition = "TIMESTAMP WITHOUT TIME ZONE"
    )
    private LocalDateTime dueDate;

    @Column(
            name = "returned_at",
            nullable = true, // the book may not be returned yet
            columnDefinition = "TIMESTAMP WITHOUT TIME ZONE"
    )
    private LocalDateTime returnedAt;

    /* RELATIONS */

    @ManyToOne(
            cascade = {
                    CascadeType.PERSIST,
                    CascadeType.MERGE
            }
    )
    @JoinColumn(
            name = "student_id",
            referencedColumnName = "id",
            foreignKey = @ForeignKey(
                    name = "rental_student_id_fk"
            )
    )
    private StudentEntity studentEntity;

    @ManyToOne(
            cascade = {
                    CascadeType.PERSIST,
                    CascadeType.MERGE
            }
    )
    @JoinColumn(
            name = "book_id",
            referencedColumnName = "id",
            foreignKey = @ForeignKey(
                    name = "rental_book_id_fk"
            )
    )
    private BookEntity bookEntity;
}
