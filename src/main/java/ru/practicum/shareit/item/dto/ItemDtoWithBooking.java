package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.booking.dto.LastAndNextBooking;
import ru.practicum.shareit.item.Comment;

import java.util.List;

@Data
@AllArgsConstructor
public class ItemDtoWithBooking {
    private long id;
    private String name;
    private String description;
    private Boolean available;
    private LastAndNextBooking lastBooking;
    private LastAndNextBooking nextBooking;
    private List<Comment> comments;
}
