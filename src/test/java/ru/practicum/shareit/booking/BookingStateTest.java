package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class BookingStateTest {

    @Test
    void values() {
        assertAll(
                () -> assertEquals("ALL", BookingState.ALL.name()),
                () -> assertEquals("CURRENT", BookingState.CURRENT.name()),
                () -> assertEquals("PAST", BookingState.PAST.name()),
                () -> assertEquals("FUTURE", BookingState.FUTURE.name()),
                () -> assertEquals("WAITING", BookingState.WAITING.name()),
                () -> assertEquals("REJECTED", BookingState.REJECTED.name())
        );
    }
}