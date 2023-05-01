package ru.practicum.shareit.item.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dto.LastAndNextBooking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;
import ru.practicum.shareit.item.repository.CommentRepository;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class MapperItem {

    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    public static Item dtoToItem(ItemDto itemDto, long userId) {
        return new Item(
                itemDto.getId(),
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                userId,
                itemDto.getRequestId()
                );
    }

    public static ItemDto itemToDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getRequestId()
        );
    }

    public ItemDtoWithBooking itemToDtoWithBooking(Item item) {

        Optional<Booking> idLast = bookingRepository.getLast(item.getId());
        Optional<Booking> idNext = bookingRepository.getNext(item.getId());

        LastAndNextBooking lastBooking = idLast.map(booking -> bookingRepository.getByIdAndItemId(booking.getId(),
                item.getId())).orElse(null);
        LastAndNextBooking nextBooking = idNext.map(booking -> bookingRepository.getByIdAndItemId(booking.getId(),
                item.getId())).orElse(null);

        return new ItemDtoWithBooking(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                lastBooking,
                nextBooking,
                commentRepository.findByItem(item.getId())
        );

    }
}
