package io.bartmilo.student.enrolment.app.domain.student.model;

import lombok.Builder;

@Builder
public record StudentIdCardDto(Long id, String cardNumber, IdCardStatus status, Long studentId) {}
