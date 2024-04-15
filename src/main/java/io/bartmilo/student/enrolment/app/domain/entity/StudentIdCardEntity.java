package io.bartmilo.student.enrolment.app.domain.entity;

import io.bartmilo.student.enrolment.app.domain.entity.StudentEntity;
import jakarta.persistence.*;
import lombok.*;

@Data
@ToString(exclude="studentEntity")
@EqualsAndHashCode(exclude = "studentEntity")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity(name = "StudentIdCard")
@Table(
        name = "student_id_card",
        uniqueConstraints = {
                @UniqueConstraint(name = "student_id_card_student_id_unique", columnNames = "card_number")
        }
)
public class StudentIdCardEntity {

    @Id
    @SequenceGenerator(
            name = "student_card_id_sequence",
            sequenceName = "student_card_id_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "student_card_id_sequence"
    )
    @Column(
            name = "id",
            updatable = false
    )
    private Long id;

    @Column(
            name = "card_number",
            nullable = false,
            length = 15
    )
    private String cardNumber;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "status",
            nullable = false
    )
    private CardStatus status;

    /* RELATIONS */

    @OneToOne(
            /*cascade = {
                    CascadeType.PERSIST,
                    CascadeType.MERGE
            },*/
            fetch = FetchType.EAGER
    )
    @JoinColumn(
            name = "student_id",
            referencedColumnName = "id",
            foreignKey = @ForeignKey(
                    name = "student_id_card_student_id_fk"
            )
    )
    private StudentEntity studentEntity;

    public enum CardStatus {
        ACTIVE, SUSPENDED, EXPIRED
    }
}
