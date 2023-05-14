package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.data.domain.Pageable;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.client.BaseClient;

import java.util.List;
import java.util.Map;

@Service
public class BookingClient extends BaseClient {
    private static final String API_PREFIX = "/bookings";

    @Autowired
    public BookingClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public BookingDtoResponse addBooking(BookingDto bookingDto, long userId) {
        return post("", userId, bookingDto);
    }

    public BookingDtoResponse processTheRequest(long userId, long bookingId, String approved) {
        return patch("/" + bookingId + "?approved={approved}", userId, approved);
    }

    public BookingDtoResponse getBooking(long bookingId, long userId) {
        return get("/" + bookingId, userId);
    }

    public List<BookingDtoResponse> getBookingListByUser(String state, long userId, Pageable pageable) {
        Map<String, Object> parameters = Map.of(
                "state", state,
                "from", pageable.getPageNumber(),
                "size", pageable.getPageSize()
        );
        return get("?state={state}&&from={from}&&size={size}", userId, parameters);
    }

    public List<BookingDtoResponse> getBookingListForThingsUser(String state, long userId, Pageable pageable) {
        Map<String, Object> parameters = Map.of(
                "state", state,
                "from", pageable.getPageNumber(),
                "size", pageable.getPageSize()
        );
        return get("/owner?state={state}&&from={from}&&size={size}", userId, parameters);
    }
}
