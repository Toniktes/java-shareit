package ru.practicum.shareit.exception;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ErrorResponseTest {

    private ErrorResponse errorResponse = new ErrorResponse("error");

    @Test
    void getError() {
        assertEquals("error", errorResponse.getError());

    }
}