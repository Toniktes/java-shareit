package ru.practicum.shareit.booking.service;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;

public interface BookingService {
    void addBooking(Booking booking, long userId);

    @Transactional
    void processTheRequest(long userId, long bookingId, boolean approved);

    @Transactional(readOnly = true)
    Booking getBooking(long id);
}
