package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.booking.BookingStatus;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BookingDto {

    private long id;
    @FutureOrPresent
    @NotNull
    LocalDateTime start;
    @FutureOrPresent
    @NotNull
    LocalDateTime end;
    long booker;
    long itemId;
    @Enumerated(EnumType.STRING)
    BookingStatus status;
}
