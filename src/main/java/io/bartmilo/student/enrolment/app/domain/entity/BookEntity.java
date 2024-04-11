package io.bartmilo.student.enrolment.app.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@ToString(exclude = "studentEntity")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity(name = "Book")
@Table(
        name = "book",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "title_unique",
                        columnNames = "title"
                ),
                @UniqueConstraint(
                        name = "isbn_unique",
                        columnNames = "isbn"
                )
        }
)
public class BookEntity {
    @Id
    @SequenceGenerator(
            name = "book_sequence",
            sequenceName = "book_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "book_sequence"
    )
    @Column(
            name = "id",
            updatable = false
    )
    private Long id;

    @Column(
            name = "book_author",
            nullable = false,
            columnDefinition = "TEXT"
    )
    private String bookAuthor;

    @Column(
            name = "title",
            nullable = false,
            columnDefinition = "TEXT"
    )
    private String title;

    @Column(
            name = "isbn",
            nullable = false,
            columnDefinition = "TEXT"
    )
    private String isbn;

    @Column(
            name = "created_at",
            nullable = false,
            columnDefinition = "TIMESTAMP WITHOUT TIME ZONE"
    )
    private LocalDateTime createdAt;

    @Column(
            name = "stock",
            nullable = true // stock can be null
    )
    private Integer stock;

    /* RELATIONS */

    @ManyToOne(
            cascade = CascadeType.PERSIST
    )
    @JoinColumn(
            name = "student_id",
            referencedColumnName = "id",
            foreignKey = @ForeignKey(
                    name = "book_student_id_fk"
            )
    )
    private StudentEntity studentEntity;
}
