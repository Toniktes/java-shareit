package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.Comment;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
@ActiveProfiles("test")
@Sql(scripts = {"file:src/main/resources/schema.sql"})
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class ItemServiceImplTests {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final ItemRequestRepository itemRequestRepository;
    private final ItemServiceImpl itemService;

    private ItemRequest itemRequest;
    private User user;
    private User user2;
    private Booking booking;
    private Item item;
    private ItemDto itemDto;
    private ItemDtoWithBooking itemDtoWithBooking;
    private Comment comment;

    @BeforeEach
    public void setUp() {
        user = new User();
        user.setName("name");
        user.setEmail("yan@mail.ru");
        user = userRepository.save(user);

        user2 = new User();
        user2.setName("name2");
        user2.setEmail("yan2@mail.ru");
        user2 = userRepository.save(user2);

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

        itemDto = new ItemDto();
        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.getAvailable());
        itemDto.setRequestId(itemRequest.getId());

        itemDtoWithBooking = new ItemDtoWithBooking();
        itemDtoWithBooking.setId(item.getId());
        itemDtoWithBooking.setName(item.getName());
        itemDtoWithBooking.setDescription(item.getDescription());
        itemDtoWithBooking.setAvailable(item.getAvailable());
        itemDtoWithBooking.setLastBooking(null);
        itemDtoWithBooking.setNextBooking(null);
        itemDtoWithBooking.setComments(Collections.emptyList());

        booking = new Booking();
        booking.setStart(LocalDateTime.now().minusHours(2).truncatedTo(ChronoUnit.MINUTES));
        booking.setEnd(LocalDateTime.now().minusHours(1).truncatedTo(ChronoUnit.MINUTES));
        booking.setBookerId(user.getId());
        booking.setItemId(item.getId());
        booking.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(booking);

        comment = new Comment();
        comment.setText("text");


    }

    @AfterEach
    public void afterEach() {
        bookingRepository.deleteAll();
        itemRepository.deleteAll();
        itemRequestRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void addItem_whenInvoked_thenSaveItem() {
        ItemDto savedItem = itemService.addItem(itemDto, user.getId());

        assertEquals(itemDto, savedItem);
    }

    @Test
    void addItem_whenNotValidIsNull_thenThrowException() {
        itemDto.setAvailable(null);

        assertThrows(ValidationException.class, () -> itemService.addItem(itemDto, user.getId()));
    }

    @Test
    void addItem_whenNotValidIsBlank_thenThrowException() {
        itemDto.setName("");

        assertThrows(ValidationException.class, () -> itemService.addItem(itemDto, user.getId()));
    }

    @Test
    void updateItem_whenValid_thenUpdateAndReturnItemDto() {
        ItemDto savedItem = itemService.updateItem(itemDto, item.getId(), user.getId());

        assertEquals(itemDto, savedItem);
    }

    @Test
    void updateItem_whenNameIsNull_thenUpdateAndReturnItemDto() {
        itemDto.setName(null);
        ItemDto savedItem = itemService.updateItem(itemDto, item.getId(), user.getId());

        assertEquals(itemDto, savedItem);
    }

    @Test
    void updateItem_whenDescriptionIsNull_thenUpdateAndReturnItemDto() {
        itemDto.setDescription(null);
        ItemDto savedItem = itemService.updateItem(itemDto, item.getId(), user.getId());

        assertEquals(itemDto, savedItem);
    }

    @Test
    void updateItem_whenAvailableIsNull_thenUpdateAndReturnItemDto() {
        itemDto.setAvailable(null);
        ItemDto savedItem = itemService.updateItem(itemDto, item.getId(), user.getId());

        assertEquals(itemDto, savedItem);
    }

    @Test
    void updateItem_whenItemIdIs0_thenUpdateAndReturnItemDto() {
        itemDto.setId(0);
        ItemDto savedItem = itemService.updateItem(itemDto, item.getId(), user.getId());

        assertEquals(itemDto, savedItem);
    }

    @Test
    void updateItem_whenUserNotOwner_thenUpdateAndReturnItemDto() {
        assertThrows(NotFoundException.class, () -> itemService.updateItem(itemDto, item.getId(),
                user.getId() + 1));
    }

    @Test
    void getItemDto_whenInvoked_thenReturnItemDto() {
        ItemDto getItemDto = itemService.getItemDto(item.getId());

        assertEquals(itemDto, getItemDto);
    }

    @Test
    void getItemDtoWithBooking_whenInvoked_thenReturnItemDtoWithBooking() {
        ItemDtoWithBooking getItemDtoWithBooking = itemService.getItemDtoWithBooking(item.getId(), user2.getId());

        assertEquals(itemDtoWithBooking, getItemDtoWithBooking);
    }

    @Test
    void getListOfThings_whenInvoked_thenReturnListItems() {
        List<ItemDtoWithBooking> getItemDtoWithBooking = itemService.getListOfThings(user.getId(), "0", "20");
        getItemDtoWithBooking.forEach(x -> x.setLastBooking(null));

        assertEquals(List.of(itemDtoWithBooking), getItemDtoWithBooking);
    }

    @Test
    void getThingsForSearch_whenInvoked_thenReturnListItemsForSearch() {
        List<ItemDto> getThingsForSearch = itemService.getThingsForSearch("name", "0", "20");

        assertEquals(List.of(itemDto), getThingsForSearch);
    }

    @Test
    void getThingsForSearch_whenEmptyText_thenReturnEmptyList() {
        List<ItemDto> getThingsForSearch = itemService.getThingsForSearch("", "0", "20");

        assertEquals(Collections.emptyList(), getThingsForSearch);
    }

    @Test
    void getThingsForSearch_whenNotValidParameters_thenThrowException() {
        assertThrows(ValidationException.class, () -> itemService.getThingsForSearch("name", "a", "b"));
    }

    @Test
    void getThingsForSearch_whenNotParametersIsNegative_thenThrowException() {
        assertThrows(ValidationException.class, () -> itemService.getThingsForSearch("name", "-1", "-1"));
    }

    @Test
    void getItem_whenInvoked_thenReturnItem() {
        Item getItem = itemService.getItem(item.getId());

        assertEquals(item, getItem);
    }

    @Test
    void addComment_whenInvoked_thenSaveAndReturnComment() {
        Comment commentNew = new Comment(
                1,
                "text",
                item.getId(),
                user.getName(),
                LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES)
        );

        Comment addingComment = itemService.addComment(comment, user.getId(), item.getId());
        addingComment.setCreated(addingComment.getCreated().truncatedTo(ChronoUnit.MINUTES));

        assertEquals(commentNew, addingComment);
    }

    @Test
    void addComment_whenNotBooking_thenThrowsException() {
        booking.setStatus(BookingStatus.REJECTED);

        assertThrows(ValidationException.class, () -> itemService.addComment(comment, user.getId(), item.getId()));
    }

    @Test
    void addComment_whenTextIsBlank_thenThrowsException() {
        comment.setText("");

        assertThrows(ValidationException.class, () -> itemService.addComment(comment, user.getId(), item.getId()));
    }

    @Test
    void addComment_whenBookingInFuture_thenThrowsException() {
        booking.setEnd(LocalDateTime.now().plusHours(1));
        bookingRepository.save(booking);

        assertThrows(ValidationException.class, () -> itemService.addComment(comment, user.getId(), item.getId()));
    }
}