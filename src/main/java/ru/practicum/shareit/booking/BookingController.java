package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;


@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingDtoResponse addBooking(@Valid @RequestBody BookingDto bookingDto, @RequestHeader("X-Sharer-User-Id") long userId) {
        log.debug("received a request to add Booking");
        return bookingService.addBooking(bookingDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDtoResponse processTheRequest(@RequestHeader("X-Sharer-User-Id") long userId,
                                                @PathVariable("bookingId") long bookingId,
                                                @RequestParam String approved) {
        log.debug("received a request to processTheRequest");
        return bookingService.processTheRequest(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDtoResponse getBooking(@PathVariable long bookingId, @RequestHeader("X-Sharer-User-Id") long userId) {
        log.debug("received a request to getBooking");
        return bookingService.getBookingDtoResponse(bookingId, userId);
    }

    @GetMapping
    public List<BookingDtoResponse> getBookingListByUser(@RequestParam(required = false, defaultValue = "ALL") String state,
                                                         @RequestHeader("X-Sharer-User-Id") long userId,
                                                         @RequestParam(defaultValue = "0") @Min(0) String from,
                                                         @RequestParam(defaultValue = "20") @Min(1) String size) {
        log.debug("received a request to getBookingList");
        return bookingService.getBookingListByUser(state, userId, PageRequest.of(Integer.parseInt(from), Integer.parseInt(size)));
    }

    @GetMapping("/owner")
    public List<BookingDtoResponse> getBookingListForThingsUser(@RequestParam(required = false, defaultValue = "ALL") String state,
                                                                @RequestHeader("X-Sharer-User-Id") long userId,
                                                                @RequestParam(defaultValue = "0") @Min(0) String from,
                                                                @RequestParam(defaultValue = "20") @Min(1) String size) {
        return bookingService.getBookingListForThingsUser(state, userId, PageRequest.of(Integer.parseInt(from), Integer.parseInt(size)));
    }

}
