package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.LastAndNextBooking;

import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByBookerId(long booker);

    List<Booking> findAllByItemId(long itemId);

    LastAndNextBooking getByIdAndItemId(long id, long itemId);

    List<Booking> findByItemIdAndBookerIdAndStatus(long itemId, long bookerId, BookingStatus status);

    @Query(value = "SELECT bk.id, bk.start_booking, bk.end_booking, bk.booker_id, bk.item_id, bk.status " +
            "FROM Booking AS bk " +
            "WHERE bk.item_Id = ?1 AND bk.start_booking < CURRENT_TIMESTAMP AND bk.status = 'APPROVED' " +
            "ORDER BY bk.start_booking DESC " +
            "LIMIT 1 ", nativeQuery = true)
    Optional<Booking> getLast(long itemId);

    @Query(value = "SELECT bk.id, bk.start_booking, bk.end_booking, bk.booker_id, bk.item_id, bk.status " +
            "FROM Booking AS bk " +
            "WHERE bk.item_Id = ?1 AND bk.start_booking > CURRENT_TIMESTAMP AND bk.status = 'APPROVED' " +
            "ORDER BY bk.start_booking " +
            "LIMIT 1 ", nativeQuery = true)
    Optional<Booking> getNext(long itemId);
}
