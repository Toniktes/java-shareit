package ru.practicum.shareit.request.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {

    ItemRequest addItemRequest(ItemRequest itemRequest, long userId);

    List<ItemRequestDto> getOwnRequestsList(long userId);

    List<ItemRequestDto> getRequestsList(Pageable pageable, long userId);

    ItemRequestDto getRequestById(long userId, long requestId);
}
