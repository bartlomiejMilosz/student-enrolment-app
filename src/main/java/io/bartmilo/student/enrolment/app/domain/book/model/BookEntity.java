package io.bartmilo.student.enrolment.app.domain.book.model;

import io.bartmilo.student.enrolment.app.domain.rental.model.RentalEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@ToString(exclude = "rentalEntityList")
@EqualsAndHashCode(exclude = "rentalEntityList")
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

    @OneToMany(
            mappedBy = "bookEntity",
            orphanRemoval = true
           /* cascade = {
                    CascadeType.PERSIST,
                    CascadeType.REMOVE
            }*/
    )
    private List<RentalEntity> rentalEntityList = new ArrayList<>();
}
