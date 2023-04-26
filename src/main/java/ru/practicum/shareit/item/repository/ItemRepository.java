package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.dto.ItemShort;
import ru.practicum.shareit.item.Item;

public interface ItemRepository extends JpaRepository<Item, Long> {

    ItemShort findByName(String name);

}
