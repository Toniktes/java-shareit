package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequest;

import javax.validation.constraints.Min;


@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final ItemRequestClient itemRequestClient;


    @PostMapping
    public ResponseEntity<Object> addItemRequest(@RequestBody ItemRequest itemRequest,
                                                 @RequestHeader("X-Sharer-User-Id") long userId) {
        log.debug("received a request to add ItemRequest");
        return itemRequestClient.addItemRequest(itemRequest, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getOwnRequestsList(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.debug("received a request to get getOwnRequestsList");
        return itemRequestClient.getOwnRequestsList(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getRequestsList(@RequestParam(defaultValue = "0") @Min(0) int from,
                                                  @RequestParam(defaultValue = "20") @Min(0) int size,
                                                  @RequestHeader("X-Sharer-User-Id") long userId) {
        log.debug("received a request to get getRequestsList");
        return itemRequestClient.getRequestsList(PageRequest.of(from, size), userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestById(@RequestHeader("X-Sharer-User-Id") long userId,
                                                 @PathVariable(value = "requestId") long requestId) {
        log.debug("received a request to get getRequestById");
        return itemRequestClient.getRequestById(userId, requestId);

    }
}
