package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.Comment;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.constraints.Min;

@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> addItem(@RequestBody ItemDto itemDto, @RequestHeader("X-Sharer-User-Id") long userId) {
        log.debug("received a request to add Item");
        return itemClient.addItem(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@PathVariable("itemId") long itemId, @RequestBody ItemDto itemDto,
                                             @RequestHeader("X-Sharer-User-Id") long userId) {
        log.debug("received a request to update Item with id: {}", itemId);
        return itemClient.updateItem(itemDto, itemId, userId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemInfo(@PathVariable("itemId") long itemId,
                                              @RequestHeader("X-Sharer-User-Id") long userId) {
        log.debug("received a request to get info for itemId: {}", itemId);
        return itemClient.getItemDtoWithBooking(itemId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getListOfThings(@RequestHeader("X-Sharer-User-Id") long userId,
                                                  @RequestParam(defaultValue = "0") @Min(0) int from,
                                                  @RequestParam(defaultValue = "20") @Min(1) int size) {
        log.debug("received a request to get list of things for userId: {}", userId);
        return itemClient.getListOfThings(userId, PageRequest.of(from, size));
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchThing(@RequestHeader("X-Sharer-User-Id") long userId,
                                              @RequestParam String text,
                                              @RequestParam(defaultValue = "0") @Min(0) int from,
                                              @RequestParam(defaultValue = "20") @Min(1) int size) {
        log.debug("received a request to search a thing by text: {}", text);
        return itemClient.getThingsForSearch(text, userId, PageRequest.of(from, size));
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestBody Comment comment, @RequestHeader("X-Sharer-User-Id") long userId,
                                             @PathVariable("itemId") long itemId) {
        log.debug("received a request to add Comment");
        return itemClient.addComment(comment, userId, itemId);
    }

}
