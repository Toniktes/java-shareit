package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
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
import ru.practicum.shareit.item.mapper.MapperItem;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserService userService;
    private final MapperItem mapperItem;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;

    @Override
    public ItemDto addItem(ItemDto itemDto, long userId) {
        validate(itemDto, userId);
        Item item = itemRepository.save(MapperItem.dtoToItem(itemDto, userId));
        return MapperItem.itemToDto(item);
    }

    private void validate(ItemDto itemDto, long userId) {
        if (itemDto.getAvailable() == null || itemDto.getName() == null || itemDto.getDescription() == null) {
            throw new ValidationException("without parameter: available or name or description");
        }
        if (itemDto.getName().isBlank() || itemDto.getDescription().isBlank()) {
            throw new ValidationException("parameter name or description is empty");
        }
        if (userService.getUser(userId) == null) {
            throw new NotFoundException("user not found");
        }
    }

    @Transactional
    @Override
    public ItemDto updateItem(ItemDto itemDto, long itemId, long userId) {
        validateUpdate(itemDto, itemId, userId);
        Item item = itemRepository.save(MapperItem.dtoToItem(itemDto, userId));
        return MapperItem.itemToDto(item);
    }

    private void validateUpdate(ItemDto itemDto, long itemId, long userId) {
        if (itemDto.getName() == null) {
            itemDto.setName(getItemDto(itemId).getName());
        }
        if (itemDto.getDescription() == null) {
            itemDto.setDescription(getItemDto(itemId).getDescription());
        }
        if (itemDto.getAvailable() == null) {
            itemDto.setAvailable(getItemDto(itemId).getAvailable());
        }
        if (itemDto.getId() == 0) {
            itemDto.setId(itemId);
        }
        if (itemRepository.getById(itemId).getOwner() != userId) {
            throw new NotFoundException("you can't change the owner of a thing");
        }
    }

    @Transactional(readOnly = true)
    @Override
    public ItemDto getItemDto(long itemId) {
        return MapperItem.itemToDto(itemRepository.getById(itemId));
    }

    @Transactional(readOnly = true)
    @Override
    public ItemDtoWithBooking getItemDtoWithBooking(long itemId, long userId) {
        ItemDtoWithBooking item = mapperItem.itemToDtoWithBooking(itemRepository.getById(itemId));
        if (itemRepository.getById(itemId).getOwner() != userId) {
            item.setLastBooking(null);
            item.setNextBooking(null);
        }
        return item;
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemDtoWithBooking> getListOfThings(long userId) {
        return itemRepository.findAllByOwner(userId)
                .stream()
                .map(mapperItem::itemToDtoWithBooking)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemDto> getThingsForSearch(String text) {
        if (text == null || text.isEmpty()) {
            return List.of();
        }
        return itemRepository.findAll()
                .stream()
                .filter(x -> x.getDescription().toLowerCase().contains(text.toLowerCase()) ||
                        x.getName().toLowerCase().contains(text.toLowerCase()))
                .filter(Item::getAvailable)
                .map(MapperItem::itemToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public Item getItem(long itemId) {
        return itemRepository.getById(itemId);
    }

    @Override
    public Comment addComment(Comment comment, long userId, long itemId) {
        if (bookingRepository.findByItemIdAndBookerIdAndStatus(itemId, userId, BookingStatus.APPROVED).isEmpty()) {
            throw new ValidationException("you can make a comment only by renting");
        }
        if (comment.getText().isBlank()) {
            throw new ValidationException("comment can't be is Empty");
        }
        List<LocalDateTime> times = bookingRepository.findByItemIdAndBookerIdAndStatus(itemId, userId, BookingStatus.APPROVED)
                .stream()
                .map(Booking::getEnd)
                .filter(x -> x.isBefore(LocalDateTime.now()))
                .collect(Collectors.toList());
        if (times.isEmpty()) {
            throw new ValidationException("can't add a review for a future booking");
        }
        comment.setAuthorName(userService.getUser(userId).getName());
        comment.setItem(itemId);
        comment.setCreated(LocalDateTime.now());
        return commentRepository.save(comment);
    }

}
