package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;


@RequiredArgsConstructor
@Service
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;

    @Override
    public ItemRequest addItemRequest(ItemRequest itemRequest, long userId) {
        validateItemRequest(itemRequest, userId);
        itemRequest.setRequestor(userId);
        itemRequest.setCreated(LocalDateTime.now());
        return itemRequestRepository.save(itemRequest);
    }

    private void validateItemRequest(ItemRequest itemRequest, long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("not found user with id: " + userId));
        if (itemRequest.getDescription().isEmpty()) {
            throw new ValidationException("description can't be empty");
        }
    }

    @Override
    public List<ItemRequestDto> getOwnRequestsList(long userId) {
        itemRequestRepository.findAllById(userId);

    }

}
