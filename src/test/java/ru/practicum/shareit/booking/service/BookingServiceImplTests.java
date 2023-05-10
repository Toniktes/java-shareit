package ru.practicum.shareit.booking.service;

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
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
@ActiveProfiles("test")
@Sql(scripts = {"file:src/main/resources/schema.sql"})
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class BookingServiceImplTests {

    private final BookingService bookingService;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final ItemRequestRepository itemRequestRepository;
    private ItemRequest itemRequest;
    private User user;
    private User user2;
    private Booking booking;
    private Item item;
    private BookingDto bookingDto;

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
        itemRequest = itemRequestRepository.save(itemRequest);

        item = new Item();
        item.setName("name");
        item.setDescription("des");
        item.setAvailable(true);
        item.setOwner(user.getId());
        item.setRequestId(itemRequest.getId());
        item = itemRepository.save(item);

        booking = new Booking();
        booking.setStart(LocalDateTime.now().plusHours(1).truncatedTo(ChronoUnit.SECONDS));
        booking.setEnd(LocalDateTime.now().plusHours(2).truncatedTo(ChronoUnit.SECONDS));
        booking.setBookerId(user.getId());
        booking.setItemId(item.getId());
        booking.setStatus(BookingStatus.WAITING);
        booking = bookingRepository.save(booking);

        bookingDto = new BookingDto();
        bookingDto.setItemId(booking.getItemId());
        bookingDto.setStart(booking.getStart());
        bookingDto.setEnd(booking.getEnd());
    }

    @AfterEach
    public void afterEach() {
        bookingRepository.deleteAll();
        itemRepository.deleteAll();
        itemRequestRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void addBooking_whenInvoked_thenSaveBooking() {
        item.setOwner(user2.getId());
        BookingDtoResponse savedBooking = bookingService.addBooking(bookingDto, user.getId());

        assertEquals(savedBooking.getId(), bookingRepository.getById(savedBooking.getId()).getId());
    }

    @Test
    void addBooking_whenOwnerEqualsUserId_thenThrowException() {
        assertThrows(NotFoundException.class, () -> bookingService.addBooking(bookingDto, user.getId()));
    }

    @Test
    void addBooking_whenItemAvailableIsFalse_thenThrowException() {
        item.setOwner(user2.getId());
        item.setAvailable(false);
        itemRepository.save(item);
        assertThrows(ValidationException.class, () -> bookingService.addBooking(bookingDto, user.getId()));
    }

    @Test
    void addBooking_whenBookingStartEqualsEnd_thenThrowException() {
        item.setOwner(user2.getId());
        bookingDto.setEnd(bookingDto.getStart());
        assertThrows(ValidationException.class, () -> bookingService.addBooking(bookingDto, user.getId()));
    }

    @Test
    void processTheRequest_whenInvoked_thenReturnBookingDtoResponse() {
        BookingDtoResponse bookingResponse = bookingService.processTheRequest(user.getId(),
                booking.getId(), "true");

        assertEquals(BookingStatus.APPROVED, bookingResponse.getStatus());
    }

    @Test
    void processTheRequest_whenApprovedIsFalse_thenReturnBookingDtoResponseWithStatusIsRejected() {
        BookingDtoResponse bookingResponse = bookingService.processTheRequest(user.getId(),
                booking.getId(), "false");

        assertEquals(BookingStatus.REJECTED, bookingResponse.getStatus());
    }

    @Test
    void processTheRequest_whenBookingStatusApproved_thenThrowException() {
        booking.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(booking);
        assertThrows(ValidationException.class, () -> bookingService.processTheRequest(user.getId(), booking.getId(), "true"));
    }

    @Test
    void processTheRequest_whenNotOwner_thenThrowException() {
        assertThrows(NotFoundException.class, () -> bookingService.processTheRequest(999, booking.getId(), "true"));
    }

    @Test
    void getBookingDtoResponse_whenInvoked_thenReturnBookingDtoResponse() {
        BookingDtoResponse bookingResponse = bookingService.getBookingDtoResponse(booking.getId(), user.getId());

        assertEquals(bookingResponse.getId(), booking.getId());
    }

    @Test
    void getBookingDtoResponse_whenBookerIdNotEqualUserId_thenThrowException() {
        assertThrows(NotFoundException.class, () -> bookingService.getBookingDtoResponse(booking.getId(), 999));
    }

    @Test
    void getBooking_whenInvoked_thenReturnBooking() {
        Booking bookingResponse = bookingService.getBooking(booking.getId());

        assertEquals(bookingResponse, booking);
    }

    @Test
    void getBookingListByUser_whenStateAll_thenReturnListOfBookings() {
        List<BookingDtoResponse> bookingResponse = bookingService.getBookingListByUser("ALL",
                user.getId(), "0", "20");

        assertEquals(bookingResponse.size(), 1);
    }

    @Test
    void getBookingListByUser_whenStateFuture_thenReturnListOfBookings() {
        List<BookingDtoResponse> bookingResponse = bookingService.getBookingListByUser("FUTURE",
                user.getId(), "0", "20");

        assertEquals(bookingResponse.size(), 1);
    }

    @Test
    void getBookingListByUser_whenStateWaiting_thenReturnListOfBookings() {
        List<BookingDtoResponse> bookingResponse = bookingService.getBookingListByUser("WAITING",
                user.getId(), "0", "20");

        assertEquals(bookingResponse.size(), 1);
    }

    @Test
    void getBookingListByUser_whenStateRejected_thenReturnListOfBookings() {
        booking.setStatus(BookingStatus.REJECTED);
        bookingRepository.save(booking);
        List<BookingDtoResponse> bookingResponse = bookingService.getBookingListByUser("REJECTED",
                user.getId(), "0", "20");

        assertEquals(bookingResponse.size(), 1);
    }

    @Test
    void getBookingListByUser_whenStateCurrent_thenReturnListOfBookings() {
        booking.setStatus(BookingStatus.REJECTED);
        booking.setStart(LocalDateTime.now().minusHours(1));
        bookingRepository.save(booking);
        List<BookingDtoResponse> bookingResponse = bookingService.getBookingListByUser("CURRENT",
                user.getId(), "0", "20");

        assertEquals(bookingResponse.size(), 1);
    }

    @Test
    void getBookingListByUser_whenStatePast_thenReturnListOfBookings() {
        booking.setStatus(BookingStatus.APPROVED);
        booking.setEnd(LocalDateTime.now().minusHours(1));
        booking.setStart(LocalDateTime.now().minusHours(2));
        bookingRepository.save(booking);
        List<BookingDtoResponse> bookingResponse = bookingService.getBookingListByUser("PAST",
                user.getId(), "0", "20");

        assertEquals(bookingResponse.size(), 1);
    }

    @Test
    void getBookingListByUser_whenStateUnsupported_thenReturnListOfBookings() {
        assertThrows(ValidationException.class, () -> bookingService.getBookingListByUser("Unknown",
                user.getId(), "0", "20"));
    }

    @Test
    void getBookingListForThingsUser_whenStateAll_thenReturnListOfBookings() {
        List<BookingDtoResponse> bookingResponse = bookingService.getBookingListForThingsUser("ALL",
                user.getId(), "0", "20");

        assertEquals(bookingResponse.size(), 1);
    }

    @Test
    void getBookingListForThingsUser_whenStateFuture_thenReturnListOfBookings() {
        List<BookingDtoResponse> bookingResponse = bookingService.getBookingListForThingsUser("FUTURE",
                user.getId(), "0", "20");

        assertEquals(bookingResponse.size(), 1);
    }

    @Test
    void getBookingListForThingsUser_whenStateFutureAndStatusApproved_thenReturnListOfBookings() {
        booking.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(booking);
        List<BookingDtoResponse> bookingResponse = bookingService.getBookingListForThingsUser("FUTURE",
                user.getId(), "0", "20");

        assertEquals(bookingResponse.size(), 1);
    }

    @Test
    void getBookingListForThingsUser_whenStateWaiting_thenReturnListOfBookings() {
        List<BookingDtoResponse> bookingResponse = bookingService.getBookingListForThingsUser("WAITING",
                user.getId(), "0", "20");

        assertEquals(bookingResponse.size(), 1);
    }

    @Test
    void getBookingListForThingsUser_whenStateRejected_thenReturnListOfBookings() {
        booking.setStatus(BookingStatus.REJECTED);
        booking.setStart(LocalDateTime.now().minusHours(1));
        bookingRepository.save(booking);
        List<BookingDtoResponse> bookingResponse = bookingService.getBookingListForThingsUser("REJECTED",
                user.getId(), "0", "20");

        assertEquals(bookingResponse.size(), 1);
    }

    @Test
    void getBookingListForThingsUser_whenStateCurrent_thenReturnListOfBookings() {
        booking.setStatus(BookingStatus.REJECTED);
        booking.setStart(LocalDateTime.now().minusHours(1));
        bookingRepository.save(booking);
        List<BookingDtoResponse> bookingResponse = bookingService.getBookingListForThingsUser("CURRENT",
                user.getId(), "0", "20");

        assertEquals(bookingResponse.size(), 1);
    }

    @Test
    void getBookingListForThingsUser_whenStatePast_thenReturnListOfBookings() {
        booking.setStatus(BookingStatus.APPROVED);
        booking.setEnd(LocalDateTime.now().minusHours(1));
        booking.setStart(LocalDateTime.now().minusHours(2));
        bookingRepository.save(booking);
        List<BookingDtoResponse> bookingResponse = bookingService.getBookingListForThingsUser("PAST",
                user.getId(), "0", "20");

        assertEquals(bookingResponse.size(), 1);
    }

    @Test
    void getBookingListForThingsUser_whenStateUnknown_thenThrowException() {
        assertThrows(ValidationException.class, () -> bookingService.getBookingListForThingsUser("Unknown",
                user.getId(), "0", "20"));
    }

    @Test
    void getBookingListForThingsUser_whenNotValidPageParam_thenThrowException() {
        assertThrows(ValidationException.class, () -> bookingService.getBookingListForThingsUser("All",
                user.getId(), "d", "d"));
    }

    @Test
    void getBookingListForThingsUser_whenPageParamIsNegative_thenThrowException() {
        assertThrows(ValidationException.class, () -> bookingService.getBookingListForThingsUser("All",
                user.getId(), "-1", "-1"));
    }
}