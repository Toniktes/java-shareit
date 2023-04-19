package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;


@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public void addBooking(@RequestBody Booking booking, @RequestHeader("X-Sharer-User-Id") long userId) {
        log.debug("received a request to add Booking");
        bookingService.addBooking(booking, userId);
    }

    @PatchMapping("/{bookingId}?approved={approved}")
    public void processTheRequest(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable("bookingId") long bookingId, @RequestParam boolean approved) {
        log.debug("received a request to processTheRequest");
        bookingService.processTheRequest(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public Booking getBooking(@PathVariable long bookingId, @RequestHeader("X-Sharer-User-Id") long userId) {
        log.debug("received a request to getBooking");
        return null;
    }

    @GetMapping("?state={state}")
    public List<Booking> getBookingList(@RequestParam BookingStatus bookingStatus, @RequestHeader("X-Sharer-User-Id") long userId) {
        log.debug("received a request to getBookingList");
        return null;
    }

}
