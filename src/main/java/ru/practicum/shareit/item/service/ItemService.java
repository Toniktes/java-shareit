package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.Comment;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;

import java.util.List;

public interface ItemService {

    ItemDto addItem(ItemDto itemDto, long userId);

    ItemDto updateItem(ItemDto itemDto, long itemId, long userId);

    ItemDto getItemDto(long itemId);

    ItemDtoWithBooking getItemDtoWithBooking(long itemId, long userId);

    List<ItemDtoWithBooking> getListOfThings(long userId);

    List<ItemDto> getThingsForSearch(String text);

    Item getItem(long itemId);

    Comment addComment(Comment comment, long userId, long itemId);

}
