package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dto.LastBooking;

import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByBookerId(long booker);

    List<Booking> findAllByItemId(long itemId);

    //LastBooking findByBooker(long booker);

    LastBooking getByIdAndItemId(long id, long itemId);


}
