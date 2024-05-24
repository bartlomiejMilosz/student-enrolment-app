package io.bartmilo.student.enrolment.app.domain.student.model;

import lombok.Builder;

@Builder
public record StudentIdCardRequest(
    Long id, String cardNumber, IdCardStatus status, Long studentId) {}
