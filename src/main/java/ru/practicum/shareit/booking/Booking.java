package ru.practicum.shareit.booking;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "booking")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "start")
    Instant start;
    @Column(name = "end")
    Instant end;
    @Column(name = "item", nullable = false)
    long item;
    @Column(name = "booker", nullable = false)
    long booker;
    @Enumerated(EnumType.STRING)
    BookingStatus status;

}
