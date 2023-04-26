package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.booking.dto.LastBooking;

@Data
@AllArgsConstructor
public class ItemDtoWithBooking {
    private long id;
    private String name;
    private String description;
    private Boolean available;
    private LastBooking lastBooking;
    private LastBooking nextBooking;
}
