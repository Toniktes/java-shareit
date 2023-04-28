package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.LastAndNextBooking;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByBookerId(long booker);

    List<Booking> findAllByItemId(long itemId);

    LastAndNextBooking getByIdAndItemId(long id, long itemId);

    List<Booking> findByItemIdAndBookerIdAndStatus(long itemId, long bookerId, BookingStatus status);

    List<Booking> findAllByItemIdAndStatus(long itemId, BookingStatus status);

}
