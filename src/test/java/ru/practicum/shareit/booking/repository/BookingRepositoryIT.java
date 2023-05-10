package ru.practicum.shareit.booking.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.LastAndNextBooking;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class BookingRepositoryIT {

    @Autowired
    BookingRepository bookingRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private ItemRequestRepository itemRequestRepository;

    private User user;
    private Booking booking;
    private ItemRequest itemRequest;
    private Item item;

    @BeforeEach
    public void beforeEach() {
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

        booking = new Booking();
        booking.setStart(LocalDateTime.now().plusHours(1).truncatedTo(ChronoUnit.SECONDS));
        booking.setEnd(LocalDateTime.now().plusHours(2).truncatedTo(ChronoUnit.SECONDS));
        booking.setBookerId(user.getId());
        booking.setItemId(user.getId());
        booking.setStatus(BookingStatus.WAITING);
        bookingRepository.save(booking);
    }

    @AfterEach
    public void afterEach() {
        bookingRepository.deleteAll();
        itemRepository.deleteAll();
        itemRequestRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void findAllByBookerIdAllState() {
        List<Booking> list = List.of(booking);
        Page<Booking> page = bookingRepository.findAllByBookerIdAllState(booking.getBookerId(),
                PageRequest.of(0, 20));

        assertEquals(list, page.getContent());

    }

    @Test
    void findAllByItemId() {
        List<Booking> list = List.of(booking);
        List<Booking> actualBooking = bookingRepository.findAllByItemId(item.getId());

        assertEquals(list, actualBooking);
    }

    @Test
    void getByIdAndItemId() {
        LastAndNextBooking lastAndNextBooking = bookingRepository.getByIdAndItemId(booking.getId(), booking.getItemId());

        assertEquals(booking.getId(), lastAndNextBooking.getId());
        assertEquals(booking.getBookerId(), lastAndNextBooking.getBookerId());
    }

    @Test
    void findByItemIdAndBookerIdAndStatus() {
        List<Booking> list = List.of(booking);
        List<Booking> actualBooking = bookingRepository.findByItemIdAndBookerIdAndStatus(item.getId(),
                item.getOwner(), BookingStatus.WAITING);

        assertEquals(list, actualBooking);
    }

    @Test
    void getLast() {
        booking.setStatus(BookingStatus.APPROVED);
        booking.setStart(LocalDateTime.now().minusDays(1));
        bookingRepository.save(booking);
        Optional<Booking> itemOptional = Optional.of(booking);
        Optional<Booking> getItem = bookingRepository.getLast(booking.getItemId());

        assertEquals(itemOptional, getItem);
    }

    @Test
    void getNext() {
        booking.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(booking);
        Optional<Booking> itemOptional = Optional.of(booking);
        Optional<Booking> getItem = bookingRepository.getNext(booking.getItemId());

        assertEquals(itemOptional, getItem);
    }

    @Test
    void getAllByBookerIdForFutureState() {
        List<Booking> list = List.of(booking);
        Page<Booking> page = bookingRepository.getAllByBookerIdForFutureState(booking.getBookerId(),
                PageRequest.of(0, 20));

        assertEquals(list, page.getContent());
    }

    @Test
    void getAllByBookerIdForWaitingState() {
        List<Booking> list = List.of(booking);
        Page<Booking> page = bookingRepository.getAllByBookerIdForWaitingState(booking.getBookerId(),
                PageRequest.of(0, 20));

        assertEquals(list, page.getContent());
    }

    @Test
    void getAllByBookerIdForRejectedState() {
        booking.setStatus(BookingStatus.REJECTED);
        bookingRepository.save(booking);
        List<Booking> list = List.of(booking);
        Page<Booking> page = bookingRepository.getAllByBookerIdForRejectedState(booking.getBookerId(),
                PageRequest.of(0, 20));

        assertEquals(list, page.getContent());
    }

    @Test
    void getAllByBookerIdForCurrentState() {
        booking.setStatus(BookingStatus.APPROVED);
        booking.setEnd(LocalDateTime.now().plusHours(1));
        booking.setStart(LocalDateTime.now().minusHours(1));
        bookingRepository.save(booking);
        List<Booking> list = List.of(booking);
        Page<Booking> page = bookingRepository.getAllByBookerIdForCurrentState(booking.getBookerId(),
                PageRequest.of(0, 20));

        assertEquals(list, page.getContent());
    }

    @Test
    void getAllByBookerIdForPastState() {
        booking.setStatus(BookingStatus.APPROVED);
        booking.setEnd(LocalDateTime.now().minusHours(1));
        booking.setStart(LocalDateTime.now().minusHours(2));
        bookingRepository.save(booking);
        List<Booking> list = List.of(booking);
        Page<Booking> page = bookingRepository.getAllByBookerIdForPastState(booking.getBookerId(),
                PageRequest.of(0, 20));

        assertEquals(list, page.getContent());
    }
}