package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;


@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequest addItemRequest(@RequestBody ItemRequest itemRequest, @RequestHeader("X-Sharer-User-Id") long userId) {
        log.debug("received a request to add ItemRequest");
        return itemRequestService.addItemRequest(itemRequest, userId);
    }

    @GetMapping
    public ItemRequestDto getOwnRequestsList(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.debug("received a request to get getOwnRequestsList");
        return itemRequestService.getOwnRequestsList(userId);
    }

}
