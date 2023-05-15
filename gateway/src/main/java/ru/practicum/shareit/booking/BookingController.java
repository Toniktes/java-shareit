package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;

import javax.validation.Valid;
import javax.validation.constraints.Min;


@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> addBooking(@Valid @RequestBody BookingDto bookingDto, @RequestHeader("X-Sharer-User-Id") long userId) {
        log.debug("received a request to add Booking");

        return bookingClient.addBooking(bookingDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> processTheRequest(@RequestHeader("X-Sharer-User-Id") long userId,
                                                    @PathVariable("bookingId") long bookingId,
                                                    @RequestParam String approved) {
        log.debug("received a request to processTheRequest");
        return bookingClient.processTheRequest(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@PathVariable long bookingId, @RequestHeader("X-Sharer-User-Id") long userId) {
        log.debug("received a request to getBooking");
        return bookingClient.getBooking(bookingId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getBookingListByUser(@RequestParam(required = false, defaultValue = "ALL") String state,
                                                       @RequestHeader("X-Sharer-User-Id") long userId,
                                                       @RequestParam(defaultValue = "0") @Min(0) int from,
                                                       @RequestParam(defaultValue = "20") @Min(1) int size) {
        log.debug("received a request to getBookingList");
        return bookingClient.getBookingListByUser(state, userId, PageRequest.of(from, size));
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getBookingListForThingsUser(@RequestParam(required = false, defaultValue = "ALL") String state,
                                                              @RequestHeader("X-Sharer-User-Id") long userId,
                                                              @RequestParam(defaultValue = "0") @Min(0) int from,
                                                              @RequestParam(defaultValue = "20") @Min(1) int size) {
        return bookingClient.getBookingListForThingsUser(state, userId, PageRequest.of(from, size));
    }

}
