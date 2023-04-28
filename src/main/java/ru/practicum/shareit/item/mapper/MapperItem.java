package ru.practicum.shareit.item.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.LastAndNextBooking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;
import ru.practicum.shareit.item.repository.CommentRepository;

import java.time.LocalDateTime;
import java.util.Comparator;

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
        LastAndNextBooking lastBooking;
        LastAndNextBooking nextBooking;

        try {
            lastBooking = bookingRepository.getByIdAndItemId((bookingRepository.findAllByItemIdAndStatus(item.getId(),
                                    BookingStatus.APPROVED)
                            .stream()
                            .filter(x -> x.getStart().isBefore(LocalDateTime.now()))
                            .max(Comparator.comparing(x -> x.getStart().getSecond())).get().getId()),
                    item.getId());
        } catch (RuntimeException e) {
            lastBooking = null;
        }

        try {
            nextBooking = bookingRepository.getByIdAndItemId(((bookingRepository.findAllByItemIdAndStatus(item.getId(),
                            BookingStatus.APPROVED)
                    .stream()
                    .filter(x -> x.getStart().isAfter(LocalDateTime.now()))
                    .max(Comparator.comparing(x -> x.getStart().getSecond())).get().getId())
            ), item.getId());
        } catch (RuntimeException e) {
            nextBooking = null;
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
