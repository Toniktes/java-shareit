package ru.practicum.shareit.booking;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "booking")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "start_booking", nullable = false)
    LocalDateTime start;
    @Column(name = "end_booking", nullable = false)
    LocalDateTime end;
    @Column(name = "booker_id", nullable = false)
    long bookerId;
    @Column(name = "item_id", nullable = false)
    long itemId;
    @Enumerated(EnumType.STRING)
    BookingStatus status;

}
