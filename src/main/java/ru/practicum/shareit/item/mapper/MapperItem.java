package ru.practicum.shareit.item.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
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
                0);
    }

    public static ItemDto itemToDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable()
        );
    }

    public ItemDtoWithBooking itemToDtoWithBooking(Item item) {
        LastAndNextBooking lastBooking = null;
        LastAndNextBooking nextBooking = null;
        Optional<Booking> idLast = Optional.empty();
        Optional<Booking> idNext = Optional.empty();

        if (!bookingRepository.findAllByItemId(item.getId()).isEmpty()) {
            if (!bookingRepository.findAllByItemIdAndStatus(item.getId(),
                    BookingStatus.APPROVED).isEmpty()) {
                idLast = bookingRepository.getLast(item.getId());
                idNext = bookingRepository.getNext(item.getId());

            }
            lastBooking = idLast.map(booking -> bookingRepository.getByIdAndItemId(booking.getId(), item.getId()))
                    .orElse(null);
            nextBooking = idNext.map(booking -> bookingRepository.getByIdAndItemId(booking.getId(), item.getId()))
                    .orElse(null);
        }
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
