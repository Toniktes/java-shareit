package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.LastAndNextBooking;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    Page<Booking> findAllByBookerIdOrderByEndDesc(long bookerId, Pageable pageable);

    Page<Booking> findAllByItemIdInOrderByStartDesc(List<Long> list, Pageable pageable);

    LastAndNextBooking getByIdAndItemId(long id, long itemId);

    List<Booking> findByItemIdAndBookerIdAndStatus(long itemId, long bookerId, BookingStatus status);

    @Query(value = "SELECT bk.id, bk.start_booking, bk.end_booking, bk.booker_id, bk.item_id, bk.status " +
            "FROM Booking AS bk " +
            "WHERE bk.item_Id = :itemId AND bk.start_booking < CURRENT_TIMESTAMP AND bk.status = 'APPROVED' " +
            "ORDER BY bk.start_booking DESC " +
            "LIMIT 1 ", nativeQuery = true)
    Optional<Booking> getLast(long itemId);

    @Query(value = "SELECT bk.id, bk.start_booking, bk.end_booking, bk.booker_id, bk.item_id, bk.status " +
            "FROM Booking AS bk " +
            "WHERE bk.item_Id = :itemId AND bk.start_booking > CURRENT_TIMESTAMP AND bk.status = 'APPROVED' " +
            "ORDER BY bk.start_booking " +
            "LIMIT 1 ", nativeQuery = true)
    Optional<Booking> getNext(long itemId);

    @Query(value = "SELECT bk.id, bk.start_booking, bk.end_booking, bk.booker_id, bk.item_id, bk.status " +
            "FROM Booking AS bk " +
            "WHERE bk.booker_id = :bookerId AND (bk.status = 'APPROVED' OR bk.status = 'WAITING') ", nativeQuery = true)
    Page<Booking> getAllByBookerIdForFutureState(long bookerId, Pageable pageable);

    @Query(value = "SELECT bk.id, bk.start_booking, bk.end_booking, bk.booker_id, bk.item_id, bk.status " +
            "FROM Booking AS bk " +
            "WHERE bk.booker_id = :bookerId AND bk.status = 'WAITING' ", nativeQuery = true)
    Page<Booking> getAllByBookerIdForWaitingState(long bookerId, Pageable pageable);

    @Query(value = "SELECT bk.id, bk.start_booking, bk.end_booking, bk.booker_id, bk.item_id, bk.status " +
            "FROM Booking AS bk " +
            "WHERE bk.booker_id = :bookerId AND bk.status = 'REJECTED' ", nativeQuery = true)
    Page<Booking> getAllByBookerIdForRejectedState(long bookerId, Pageable pageable);

    @Query(value = "SELECT bk.id, bk.start_booking, bk.end_booking, bk.booker_id, bk.item_id, bk.status " +
            "FROM Booking AS bk " +
            "WHERE bk.booker_id = :bookerId AND (bk.status = 'REJECTED' OR bk.status = 'APPROVED') AND " +
            "(bk.end_booking > CURRENT_TIMESTAMP AND bk.start_booking < CURRENT_TIMESTAMP) ", nativeQuery = true)
    Page<Booking> getAllByBookerIdForCurrentState(long bookerId, Pageable pageable);

    @Query(value = "SELECT bk.id, bk.start_booking, bk.end_booking, bk.booker_id, bk.item_id, bk.status " +
            "FROM Booking AS bk " +
            "WHERE bk.booker_id = :bookerId AND bk.status = 'APPROVED' AND " +
            "(bk.end_booking < CURRENT_TIMESTAMP AND bk.start_booking < CURRENT_TIMESTAMP) ", nativeQuery = true)
    Page<Booking> getAllByBookerIdForPastState(long bookerId, Pageable pageable);

}
