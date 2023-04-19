package ru.practicum.shareit.booking;

import lombok.Data;

import javax.persistence.*;
import java.time.Instant;

@Data
@Entity
@Table(name = "booking")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "start_booking")
    Instant start;
    @Column(name = "end_booking")
    Instant end;
    @Column(name = "item", nullable = false)
    long item;
    @Column(name = "booker", nullable = false)
    long booker;
    @Enumerated(EnumType.STRING)
    BookingStatus status;

}
