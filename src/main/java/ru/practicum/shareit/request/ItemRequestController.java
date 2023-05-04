package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;


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
    public List<ItemRequestDto> getOwnRequestsList(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.debug("received a request to get getOwnRequestsList");
        return itemRequestService.getOwnRequestsList(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getRequestsList(@RequestParam(defaultValue = "0") String from, @RequestParam(defaultValue = "20") String size,
                                                @RequestHeader("X-Sharer-User-Id") long userId) {
        log.debug("received a request to get getRequestsList");
        return itemRequestService.getRequestsList(from, size, userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getRequestById(@RequestHeader("X-Sharer-User-Id") long userId,
                                         @PathVariable(value = "requestId") long requestId) {
        log.debug("received a request to get getRequestById");
        return itemRequestService.getRequestById(userId, requestId);

    }


}
