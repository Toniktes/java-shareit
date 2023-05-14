package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.MapperItem;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.MapperItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
@ActiveProfiles("test")
@Sql(scripts = {"file:src/main/resources/schema.sql"})
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class ItemRequestServiceImplIT {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final ItemRequestRepository itemRequestRepository;
    private final ItemRequestService itemRequestService;

    private User user;
    private Item item;
    private ItemRequest itemRequest;

    @BeforeEach
    public void setUp() {
        user = new User();
        user.setName("name");
        user.setEmail("yan@mail.ru");
        user = userRepository.save(user);

        itemRequest = new ItemRequest();
        itemRequest.setDescription("des");
        itemRequest.setRequestor(user.getId());
        itemRequest.setCreated(LocalDateTime.now());
        itemRequestRepository.save(itemRequest);

        item = new Item();
        item.setName("name");
        item.setDescription("des");
        item.setAvailable(true);
        item.setOwner(user.getId());
        item.setRequestId(itemRequest.getId());
        itemRepository.save(item);
    }

    @AfterEach
    public void afterEach() {
        bookingRepository.deleteAll();
        itemRepository.deleteAll();
        itemRequestRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void addItemRequest_whenValid_thenSaveAndReturn() {
        ItemRequest savedItemRequest = itemRequestService.addItemRequest(itemRequest, user.getId());

        assertEquals(itemRequest, savedItemRequest);
    }

    @Test
    void addItemRequest_whenNotValidDescriptionIsNull_thenSaveAndReturn() {
        itemRequest = new ItemRequest();
        itemRequest.setRequestor(user.getId());
        itemRequest.setCreated(LocalDateTime.now());

        assertThrows(ValidationException.class, () -> itemRequestService.addItemRequest(itemRequest, user.getId()));
    }

    @Test
    void addItemRequest_whenNotValidDescriptionIsBlank_thenSaveAndReturn() {
        itemRequest.setDescription("");

        assertThrows(ValidationException.class, () -> itemRequestService.addItemRequest(itemRequest, user.getId()));
    }

    @Test
    void getOwnRequestsList_whenInvoked_thenReturnListItemRequestDto() {
        List<ItemDto> itemDtos = Stream.of(item).map(MapperItem::itemToDto).collect(Collectors.toList());
        ItemRequestDto itemRequestDto = MapperItemRequest.toDto(itemRequest);
        itemRequestDto.setItems(itemDtos);
        List<ItemRequestDto> list = List.of(itemRequestDto);

        List<ItemRequestDto> itemRequestDtoList = itemRequestService.getOwnRequestsList(user.getId());

        assertEquals(list, itemRequestDtoList);
    }

    @Test
    void getRequestsList_whenValidParameters_thenReturnListItemRequestDto() {
        List<ItemDto> itemDtos = Stream.of(item).map(MapperItem::itemToDto).collect(Collectors.toList());
        ItemRequestDto itemRequestDto = MapperItemRequest.toDto(itemRequest);
        itemRequestDto.setItems(itemDtos);
        List<ItemRequestDto> list = List.of(itemRequestDto);

        List<ItemRequestDto> itemRequestDtoList = itemRequestService.getRequestsList(PageRequest.of(0, 20),
                user.getId() + 1);

        assertEquals(list, itemRequestDtoList);
    }

    @Test
    void getRequestsList_whenParametersIsNegative_thenThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> itemRequestService.getRequestsList(PageRequest.of(-1, -1),
                user.getId() + 1));
    }

    @Test
    void getRequestById_whenInvoked_thenReturnItemRequestDto() {
        List<ItemDto> itemDtos = Stream.of(item).map(MapperItem::itemToDto).collect(Collectors.toList());
        ItemRequestDto itemRequestDto = MapperItemRequest.toDto(itemRequest);
        itemRequestDto.setItems(itemDtos);
        ItemRequestDto actualRequestDto = itemRequestService.getRequestById(user.getId(), itemRequest.getId());

        assertEquals(itemRequestDto, actualRequestDto);
    }

    @Test
    void getRequestById_whenNotValidUserId_thenThrowException() {
        assertThrows(NotFoundException.class, () -> itemRequestService.getRequestById(999, itemRequest.getId()));
    }

    @Test
    void getRequestById_whenNotValidRequestId_thenThrowException() {
        assertThrows(NotFoundException.class, () -> itemRequestService.getRequestById(user.getId(), 999));
    }
}