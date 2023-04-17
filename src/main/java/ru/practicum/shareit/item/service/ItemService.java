package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    ItemDto addItem(ItemDto itemDto, long userId);

    ItemDto updateItem(ItemDto itemDto, long itemId, long userId);

    ItemDto getItemDto(long itemId);

    List<ItemDto> getListOfThings(long userId);

    List<ItemDto> getThingsForSearch(String text);
}
