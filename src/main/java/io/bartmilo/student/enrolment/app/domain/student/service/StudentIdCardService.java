package io.bartmilo.student.enrolment.app.domain.student.service;

import io.bartmilo.student.enrolment.app.domain.student.model.StudentEntity;
import io.bartmilo.student.enrolment.app.domain.student.model.StudentIdCardEntity;

public interface StudentIdCardService {

    /**
     * Generates a new ID card for the given student. This method is responsible for creating
     * a new {@link StudentIdCardEntity} that represents an ID card issued to a student.
     * The ID card contains details such as the card's unique identifier, issue date, and status.
     *
     * @param studentEntity the {@link StudentEntity} for whom the ID card is to be generated
     * @return a {@link StudentIdCardEntity} representing the newly created student ID card,
     *         which includes all relevant details such as card number, issue date, and status
     */
    StudentIdCardEntity generateStudentIdCard(StudentEntity studentEntity);
}
