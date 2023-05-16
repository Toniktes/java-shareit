package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.Item;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    ItemShort findByName(String name);

    interface ItemShort {
        long getId();

        String getName();
    }

    Page<Item> findAllByOwner(long id, Pageable pageable);

    List<Item> findAllByRequestId(long requestId);

    @Query(value = "SELECT it.id " +
            "FROM items AS it " +
            "WHERE it.owner = :userId", nativeQuery = true)
    List<Long> getListItemIdByUser(long userId);

    List<Item> findAllByOwner(long userId);

}
