package ru.practicum.shareit.booking.dto;

import lombok.*;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.UserShort;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class BookingDtoResponse {
    private long id;
    LocalDateTime start;
    LocalDateTime end;
    UserShort booker;
    ItemRepository.ItemShort item;
    BookingStatus status;
}
