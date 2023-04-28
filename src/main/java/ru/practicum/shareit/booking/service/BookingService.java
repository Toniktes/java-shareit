package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;

import java.util.List;

public interface BookingService {
    BookingDtoResponse addBooking(BookingDto bookingDto, long userId);

    BookingDtoResponse processTheRequest(long userId, long bookingId, String approved);

    BookingDtoResponse getBooking(long bookingId, long userId);

    Booking getBooking(long bookingId);

    List<BookingDtoResponse> getBookingListByUser(String state, long userId);

    List<BookingDtoResponse> getBookingListForThingsUser(String state, long userId);
}
