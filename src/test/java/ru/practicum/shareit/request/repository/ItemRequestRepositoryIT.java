package ru.practicum.shareit.request.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class ItemRequestRepositoryIT {

    @Autowired
    ItemRequestRepository itemRequestRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    BookingRepository bookingRepository;

    private User user;
    private ItemRequest itemRequest;

    @BeforeEach
    public void beforeEach() {
        user = new User();
        user.setName("name");
        user.setEmail("yan@mail.ru");
        user = userRepository.save(user);

        itemRequest = itemRequestRepository.save(ItemRequest.builder()
                .description("des")
                .requestor(user.getId())
                .created(LocalDateTime.now())
                .build());
    }

    @AfterEach
    public void afterEach() {
        bookingRepository.deleteAll();
        itemRepository.deleteAll();
        itemRequestRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void findAllByRequestor() {
        List<ItemRequest> list = List.of(itemRequest);
        List<ItemRequest> requestList = itemRequestRepository.findAllByRequestor(itemRequest.getId());

        assertEquals(list, requestList);
    }

    @Test
    void findAllByIdIsNot() {
        List<ItemRequest> list = List.of(itemRequest);
        Page<ItemRequest> requestList = itemRequestRepository.findAllByIdIsNot(itemRequest.getId() + 1,
                PageRequest.of(0, 20));

        assertEquals(list, requestList.getContent());
    }
}