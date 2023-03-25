package ru.practicum.shareit.item.storage;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ItemStorageImpl implements ItemStorage {

    private final Map<Long, Item> items = new HashMap<>();

    @Override
    public Item addItem(Item item) {
        items.put(item.getId(), item);
        return items.get(item.getId());
    }

    @Override
    public Item getItem(long itemId) {
        return items.get(itemId);
    }

    @Override
    public Item updateItem(Item item) {
        Item oldItem = items.get(item.getId());
        item.setRequest(oldItem.getRequest());
        items.put(item.getId(), item);
        return items.get(item.getId());
    }

    @Override
    public List<Item> getAllItems() {
        return new ArrayList<>(items.values());
    }
}
