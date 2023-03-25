package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorage {

    Item addItem(Item item);

    Item getItem(long itemId);

    Item updateItem(Item item);

    List<Item> getAllItems();
}
