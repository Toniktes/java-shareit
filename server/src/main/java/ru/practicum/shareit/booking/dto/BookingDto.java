package ru.practicum.shareit.booking.dto;

import lombok.*;
import ru.practicum.shareit.booking.BookingStatus;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BookingDto {

    private long id;
    LocalDateTime start;
    LocalDateTime end;
    long booker;
    long itemId;
    @Enumerated(EnumType.STRING)
    BookingStatus status;
}
