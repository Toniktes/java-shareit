package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dto.ItemShort;

import java.util.List;
import java.util.stream.Collectors;

public interface ItemRepository extends JpaRepository<Item, Long> {

    ItemShort findByName(String name);
    Page<Item> findAll(Pageable pageable);
    Page<Item> findAllByOwner(long id, Pageable pageable);
    List<Item> findAllByOwner(long id);
    List<Item> findAllByRequestId(long requestId);

    @Query(value = "SELECT it.id " +
            "FROM items AS it " +
            "WHERE it.owner = ?1", nativeQuery = true)
    List<Long> getListItemIdByUser(long userId);



}
