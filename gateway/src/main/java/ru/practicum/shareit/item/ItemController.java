package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.Comment;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    public ItemDto addItem(@RequestBody ItemDto itemDto, @RequestHeader("X-Sharer-User-Id") long userId) {
        log.debug("received a request to add Item");
        return itemClient.addItem(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@PathVariable("itemId") long itemId, @RequestBody ItemDto itemDto,
                              @RequestHeader("X-Sharer-User-Id") long userId) {
        log.debug("received a request to update Item with id: {}", itemId);
        return itemClient.updateItem(itemDto, itemId, userId);
    }

    @GetMapping("/{itemId}")
    public ItemDtoWithBooking getItemInfo(@PathVariable("itemId") long itemId,
                                          @RequestHeader("X-Sharer-User-Id") long userId) {
        log.debug("received a request to get info for itemId: {}", itemId);
        return itemClient.getItemDtoWithBooking(itemId, userId);
    }

    @GetMapping
    public List<ItemDtoWithBooking> getListOfThings(@RequestHeader("X-Sharer-User-Id") long userId,
                                                    @RequestParam(defaultValue = "0") int from,
                                                    @RequestParam(defaultValue = "20") int size) {
        log.debug("received a request to get list of things for userId: {}", userId);
        return itemClient.getListOfThings(userId, PageRequest.of(from, size));

    }

    @GetMapping("/search")
    public List<ItemDto> searchThing(@RequestParam String text,
                                     @RequestParam(defaultValue = "0") int from,
                                     @RequestParam(defaultValue = "20") int size) {
        log.debug("received a request to search a thing by text: {}", text);
        return itemClient.getThingsForSearch(text, PageRequest.of(from, size));
    }

    @PostMapping("/{itemId}/comment")
    public Comment addComment(@RequestBody Comment comment, @RequestHeader("X-Sharer-User-Id") long userId,
                              @PathVariable("itemId") long itemId) {
        log.debug("received a request to add Comment");
        return itemClient.addComment(comment, userId, itemId);
    }

}
