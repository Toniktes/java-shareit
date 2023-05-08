package ru.practicum.shareit.booking;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.service.BookingService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class BookingControllerTest {

    @Mock
    private BookingService bookingService;
    @InjectMocks
    BookingController bookingController;
    private ObjectMapper mapper;
    private MockMvc mockMvc;
    private BookingDtoResponse bookingDtoResponse;
    private BookingDto bookingDto;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(bookingController)
                .build();

        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        bookingDtoResponse = new BookingDtoResponse(
                1,
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2),
                null,
                null,
                BookingStatus.WAITING
        );
        bookingDto = new BookingDto(
                1,
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2),
                1L,
                1L,
                BookingStatus.WAITING
        );
    }

    @SneakyThrows
    @Test
    @JsonFormat(pattern = "dd-MM-yyyy")
    void addBooking_whenInvoked_thenReturnStatusOkWithCreatedBookingInBody() {
        when(bookingService.addBooking(any(), anyLong()))
                .thenReturn(bookingDtoResponse);

        mockMvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingDto))
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDtoResponse.getId()), Long.class))
                .andExpect(jsonPath("$.booker", is(bookingDtoResponse.getBooker())))
                .andExpect(jsonPath("$.item", is(bookingDtoResponse.getItem())))
                .andExpect(jsonPath("$.status", is(bookingDtoResponse.getStatus().name())));
    }


    @SneakyThrows
    @Test
    void processTheRequest() {
        when(bookingService.processTheRequest(anyLong(), anyLong(), anyString()))
                .thenReturn(bookingDtoResponse);

        mockMvc.perform(patch("/bookings/{bookingId}", bookingDtoResponse.getId())
                        .header("X-Sharer-User-Id", 1)
                        .param("approved", "true")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDtoResponse.getId()), Long.class))
                .andExpect(jsonPath("$.booker", is(bookingDtoResponse.getBooker())))
                .andExpect(jsonPath("$.item", is(bookingDtoResponse.getItem())))
                .andExpect(jsonPath("$.status", is(bookingDtoResponse.getStatus().name())));
    }

    @SneakyThrows
    @Test
    void getBooking() {
        when(bookingService.getBookingDtoResponse(anyLong(), anyLong()))
                .thenReturn(bookingDtoResponse);

        mockMvc.perform(get("/bookings/{bookingId}", bookingDtoResponse.getId())
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDtoResponse.getId()), Long.class))
                .andExpect(jsonPath("$.booker", is(bookingDtoResponse.getBooker())))
                .andExpect(jsonPath("$.item", is(bookingDtoResponse.getItem())))
                .andExpect(jsonPath("$.status", is(bookingDtoResponse.getStatus().name())));

    }

    @SneakyThrows
    @Test
    void getBookingListByUser() {
        List<BookingDtoResponse> listBooking = List.of(bookingDtoResponse);
        when(bookingService.getBookingListByUser(anyString(), anyLong(), anyString(), anyString()))
                .thenReturn(listBooking);

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(listBooking)));
    }

    @SneakyThrows
    @Test
    void getBookingListForThingsUser() {
        List<BookingDtoResponse> listBooking = List.of(bookingDtoResponse);
        when(bookingService.getBookingListForThingsUser(anyString(), anyLong(), anyString(), anyString()))
                .thenReturn(listBooking);

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1)
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(listBooking)));
    }
}