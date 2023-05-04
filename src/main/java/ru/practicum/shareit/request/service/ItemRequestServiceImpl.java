package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.MapperItem;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.MapperItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@RequiredArgsConstructor
@Service
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public ItemRequest addItemRequest(ItemRequest itemRequest, long userId) {
        validateItemRequest(itemRequest, userId);
        itemRequest.setRequestor(userId);
        itemRequest.setCreated(LocalDateTime.now());
        return itemRequestRepository.save(itemRequest);
    }

    private void validateItemRequest(ItemRequest itemRequest, long userId) {
        validateUser(userId);
        if (itemRequest.getDescription() == null) {
            throw new ValidationException("description can't be null");
        }
        if (itemRequest.getDescription().isBlank()) {
            throw new ValidationException("description can't be empty");
        }
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemRequestDto> getOwnRequestsList(long userId) {
        validateUser(userId);
        List<ItemRequestDto> list = itemRequestRepository.findAllByRequestor(userId)
                .stream()
                .map(MapperItemRequest::toDto)
                .collect(Collectors.toList());

        for (ItemRequestDto item : list) {
            item.setItems(
                    new ArrayList<>(
                            (Optional.of(itemRepository.findAllByRequestId(item.getId()))
                                    .orElse(Collections.emptyList()))
                                    .stream()
                                    .map(MapperItem::itemToDto)
                                    .collect(Collectors.toList()))
            );
        }
        return list.stream().sorted(Comparator.comparing(ItemRequestDto::getCreated).reversed()).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemRequestDto> getRequestsList(String from, String size, long userId) {
        int parseFrom;
        int parseSize;
        try {
            parseFrom = Integer.parseInt(from);
            parseSize = Integer.parseInt(size);
        } catch (NumberFormatException e) {
            throw new ValidationException("from or size not a number");
        }
        if (parseFrom < 0 || parseSize <= 0) {
            throw new ValidationException("from can't be < 0 and size can't be <= 0");
        }
        List<ItemRequestDto> list = itemRequestRepository.findAllByIdIsNot(userId)
                .stream()
                .map(MapperItemRequest::toDto)
                .collect(Collectors.toList());

        for (ItemRequestDto item : list) {
            item.setItems(
                    new ArrayList<>(
                            (Optional.of(itemRepository.findAllByRequestId(item.getId()))
                                    .orElse(Collections.emptyList()))
                                    .stream()
                                    .map(MapperItem::itemToDto)
                                    .collect(Collectors.toList()))
            );
        }
        return list.stream().sorted(Comparator.comparing(ItemRequestDto::getCreated).reversed()).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public ItemRequestDto getRequestById(long userId, long requestId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("not found user with id: " + userId));
        itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("not found request with id: " + requestId));

        List<Item> items = itemRepository.findAllByRequestId(requestId);
        List<ItemDto> itemDtos = items.stream().map(MapperItem::itemToDto).collect(Collectors.toList());

        ItemRequestDto itemRequestDto = MapperItemRequest.toDto(itemRequestRepository.getById(requestId));

        itemRequestDto.setItems(itemDtos);

        return itemRequestDto;

    }

    private void validateUser(long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("not found user with id: " + userId));
    }


}
