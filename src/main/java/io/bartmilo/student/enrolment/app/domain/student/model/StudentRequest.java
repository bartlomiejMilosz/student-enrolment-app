package io.bartmilo.student.enrolment.app.domain.student.model;

import lombok.Builder;

@Builder
public record StudentRequest(String firstName, String lastName, String email, Integer age) {}
