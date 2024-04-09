package io.bartmilo.student.enrolment.app.web;

import io.bartmilo.student.enrolment.app.web.controller.StudentController;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@Order(Ordered.LOWEST_PRECEDENCE)
@ControllerAdvice(assignableTypes = {StudentController.class})
public class AppExceptionControllerAdvice {

    private static final Logger LOGGER = LoggerFactory.getLogger(
            AppExceptionControllerAdvice.class);

    @ExceptionHandler(IndexOutOfBoundsException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleIndexOutOfBoundsException(
            HttpServletRequest request,
            IndexOutOfBoundsException e
    ) {
        LOGGER.error("Index out of bounds error: {}", e.getMessage());

        return String.format(
                "Global - [%s] Cannot process %s request at %s: index out of bounds.",
                AppExceptionControllerAdvice.class.getSimpleName(),
                request.getMethod(),
                request.getRequestURL());
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleEntityNotFound(
            HttpServletRequest request,
            EntityNotFoundException e
    ) {
        LOGGER.error("Entity not found: {}", e.getMessage());

        return String.format(
                "Global - [%s] Cannot find the requested entity at %s: %s",
                AppExceptionControllerAdvice.class.getSimpleName(),
                request.getRequestURL(),
                e.getMessage());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleMessageNotReadableException(
            HttpServletRequest request,
            HttpMessageNotReadableException e
    ) {
        LOGGER.error("Message not readable error: {}", e.getMessage());

        return String.format(
                "Global - [%s] Error processing %s request at %s: message not readable.",
                AppExceptionControllerAdvice.class.getSimpleName(),
                request.getMethod(),
                request.getRequestURL());
    }


    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleAllException(
            HttpServletRequest request,
            Exception e
    ) {
        LOGGER.error("An unexpected error occurred: {}", e.getMessage());

        return String.format(
                "Global - [%s] An unexpected error occurred while processing %s request at %s: %s",
                AppExceptionControllerAdvice.class.getSimpleName(),
                request.getMethod(),
                request.getRequestURL(),
                e.getMessage());
    }
}
