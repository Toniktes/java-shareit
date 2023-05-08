package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dto.ItemShort;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
@DataJpaTest
class ItemRepositoryIT {

    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRequestRepository itemRequestRepository;

    private User user;
    private ItemRequest itemRequest;
    private Item item;

    @BeforeEach
    public void beforeEach() {
        user = userRepository.save(User.builder()
                .name("name")
                .email("yan@mail.ru")
                .build());
        itemRequest = itemRequestRepository.save(ItemRequest.builder()
                .description("des")
                .requestor(user.getId())
                .created(LocalDateTime.now())
                .build());
        item = itemRepository.save(Item.builder()
                .name("name")
                .description("des")
                .available(true)
                .owner(user.getId())
                .requestId(itemRequest.getId())
                .build()
        );
    }

    @AfterEach
    public void afterEach() {
        itemRepository.deleteAll();
        itemRequestRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void findByName() {
        ItemShort itemShort = itemRepository.findByName("name");

        assertEquals("name", itemShort.getName());
    }

    @Test
    void findAllByOwner() {
        List<Item> itemList = List.of(item);
        Page<Item> items = itemRepository.findAllByOwner(user.getId(), PageRequest.of(0, 20));

        assertEquals(itemList, items.getContent());
    }

    @Test
    void findAllByRequestId() {
        List<Item> itemList = List.of(item);
        List<Item> list = itemRepository.findAllByRequestId(itemRequest.getId());

        assertEquals(itemList, list);
    }

    @Test
    void getListItemIdByUser() {
        List<Long> idList = List.of(item.getOwner());
        List<Long> ids = itemRepository.getListItemIdByUser(item.getOwner());

        assertEquals(idList, ids);
    }
}