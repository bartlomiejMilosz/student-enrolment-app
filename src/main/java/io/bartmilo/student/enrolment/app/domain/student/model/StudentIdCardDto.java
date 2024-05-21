package io.bartmilo.student.enrolment.app.domain.student.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentIdCardDto {

    private Long id;
    private String cardNumber;
    private CardStatus status;
    private Long studentId; // This can be used to associate the ID card with a student

    public enum CardStatus {
        ACTIVE, SUSPENDED, EXPIRED
    }
}
