package ru.practicum.shareit.exception;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.persistence.EntityNotFoundException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class ExceptionHandlersTest {

    @Autowired
    private ExceptionHandlers exceptionHandlers;

    @Test
    void validationException() {
        ErrorResponse errorResponse = exceptionHandlers.validationException(new ValidationException("error"));

        assertEquals(errorResponse.getError(), "error");
    }

    @Test
    void notFoundException() {
        ErrorResponse errorResponse = exceptionHandlers.notFoundException(new NotFoundException("error"));

        assertEquals(errorResponse.getError(), "error");
    }

    @Test
    void entityNotFoundException() {
        ErrorResponse errorResponse = exceptionHandlers.entityNotFoundException(new EntityNotFoundException("error"));

        assertEquals(errorResponse.getError(), "error");
    }
}