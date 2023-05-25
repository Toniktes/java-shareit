package ru.practicum.shareit.booking.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;

@Component
@RequiredArgsConstructor
public class MapperBooking {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    public static Booking dtoToBooking(BookingDto bookingDto) {
        return new Booking(
                bookingDto.getId(),
                bookingDto.getStart(),
                bookingDto.getEnd(),
                bookingDto.getBooker(),
                bookingDto.getItemId(),
                bookingDto.getStatus()
        );
    }

    public BookingDtoResponse bookingToDtoResponse(Booking booking, Item item) {
        return new BookingDtoResponse(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                userRepository.findByNameAndId(userRepository.getById(booking.getBookerId()).getName(), booking.getBookerId()),
                itemRepository.findByName(item.getName()),
                booking.getStatus()
        );

    }
}
