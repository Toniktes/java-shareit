package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;

@RequiredArgsConstructor
@Service
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;

    @Transactional
    @Override
    public void addBooking(Booking booking, long userId) {
        bookingRepository.save(booking);

    }

    @Transactional
    @Override
    public void processTheRequest(long userId, long bookingId, boolean approved) {
        Booking booking = getBooking(bookingId);
        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }
        if (booking.getBooker() == 0) {
            booking.setBooker(bookingId);
        }
        bookingRepository.save(booking);
    }

    @Transactional(readOnly = true)
    @Override
    public Booking getBooking(long id) {
        return bookingRepository.getById(id);
    }

    private void validate() {

    }
}
