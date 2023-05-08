package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.mapper.MapperBooking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

/*    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemService itemService;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private MapperBooking mapperBooking;

    @InjectMocks
    @Spy
    private BookingServiceImpl bookingService;

    private Booking booking;
    private BookingDto bookingDto;
    private BookingDtoResponse bookingDtoResponse;
    private Item item;

    @BeforeEach
    void setUp() {
        booking = Booking.builder()
                .id(1)
                .start(LocalDateTime.now().plusHours(1).truncatedTo(ChronoUnit.SECONDS))
                .end(LocalDateTime.now().plusHours(2).truncatedTo(ChronoUnit.SECONDS))
                .bookerId(1)
                .itemId(1)
                .status(BookingStatus.WAITING)
                .build();

        bookingDtoResponse = BookingDtoResponse.builder()
                .id(1)
                .start(LocalDateTime.now().plusHours(1).truncatedTo(ChronoUnit.SECONDS))
                .end(LocalDateTime.now().plusHours(2).truncatedTo(ChronoUnit.SECONDS))
                .booker(null)
                .item(null)
                .status(BookingStatus.WAITING)
                .build();

        bookingDto = BookingDto.builder()
                .id(1)
                .start(LocalDateTime.now().plusHours(1).truncatedTo(ChronoUnit.SECONDS))
                .end(LocalDateTime.now().plusHours(2).truncatedTo(ChronoUnit.SECONDS))
                .booker(1)
                .itemId(1)
                .status(BookingStatus.WAITING)
                .build();

        item = Item.builder()
                .name("name")
                .description("des")
                .available(true)
                .owner(1)
                .requestId(1)
                .build();

    }

    @Test
    void addBooking() {
        doNothing().doThrow().when(bookingService).validateBooking(any(), anyLong());
        when(bookingRepository.save(any()))
                .thenReturn(booking);
        when(mapperBooking.bookingToDtoResponse(any(), any()))
                .thenReturn(bookingDtoResponse);
        BookingDtoResponse actualBooking = bookingService.addBooking(bookingDto, 1);

        assertEquals(bookingDtoResponse, actualBooking);
        verify(bookingRepository).save(booking);
    }

    @Test
    void processTheRequest() {
        *//*when(bookingRepository.getById(anyLong()))
                .thenReturn(booking);
        when(itemService.getItem(any()))
                .thenReturn(item);
        when(mapperBooking.bookingToDtoResponse(any(), any()))
                .thenReturn(bookingDtoResponse);
        BookingDtoResponse actualBooking = bookingService.processTheRequest(1,1, "true");

        assertEquals(bookingDtoResponse, actualBooking);*//*
        //verify(bookingRepository).save(booking);

    }

    @Test
    void getBooking() {
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(booking));
        when(mapperBooking.bookingToDtoResponse(any(), any()))
                .thenReturn(bookingDtoResponse);

        BookingDtoResponse actualBooking = bookingService.getBookingDtoResponse(1, 1);

        assertEquals(bookingDtoResponse, actualBooking);
    }

    @Test
    void testGetBooking() {
        when(bookingRepository.getById(anyLong()))
                .thenReturn(booking);

        Booking actualBooking = bookingService.getBooking(booking.getId());

        assertEquals(booking, actualBooking);
    }

    @Test
    void getBookingListByUser() {
        List<BookingDtoResponse> bookingsDto = List.of(bookingDtoResponse);
        List<Booking> bookings = List.of(booking);
        Page<Booking> page = new PageImpl<>(bookings);
        when(bookingRepository.findAllByBookerIdAllState(anyLong(), any()))
                .thenReturn(page);
        when(itemService.getItem(anyLong()))
                .thenReturn(item);
        doNothing().doThrow(new NullPointerException()).when(mapperBooking).bookingToDtoResponse(any(), any());
        when(bookingService.mapAndSorted(any()))
                .thenReturn(bookingsDto);


        List<BookingDtoResponse> result = bookingService.getBookingListByUser("ALL", 1, "0", "20");

    }

    @Test
    void getBookingListForThingsUser() {
    }*/
}