package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.dto.ItemShort;
import ru.practicum.shareit.user.UserShort;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class BookingDtoResponse {
    private long id;
    LocalDateTime start;
    LocalDateTime end;
    UserShort booker;
    ItemShort item;
    @Enumerated(EnumType.STRING)
    BookingStatus status;
}
