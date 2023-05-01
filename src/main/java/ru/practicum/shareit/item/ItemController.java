package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDto addItem(@RequestBody ItemDto itemDto, @RequestHeader("X-Sharer-User-Id") long userId) {
        log.debug("received a request to add Item");
        return itemService.addItem(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@PathVariable("itemId") long itemId, @RequestBody ItemDto itemDto, @RequestHeader("X-Sharer-User-Id") long userId) {
        log.debug("received a request to update Item with id: {}", itemId);
        return itemService.updateItem(itemDto, itemId, userId);
    }

    @GetMapping("/{itemId}")
    public ItemDtoWithBooking getItemInfo(@PathVariable("itemId") long itemId, @RequestHeader("X-Sharer-User-Id") long userId) {
        log.debug("received a request to get info for itemId: {}", itemId);
        return itemService.getItemDtoWithBooking(itemId, userId);
    }

    @GetMapping
    public List<ItemDtoWithBooking> getListOfThings(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.debug("received a request to get list of things for userId: {}", userId);
        return itemService.getListOfThings(userId);

    }

    @GetMapping("/search")
    public List<ItemDto> searchThing(@RequestParam String text) {
        log.debug("received a request to search a thing by text: {}", text);
        return itemService.getThingsForSearch(text);
    }

    @PostMapping("/{itemId}/comment")
    public Comment addComment(@RequestBody Comment comment, @RequestHeader("X-Sharer-User-Id") long userId,
                              @PathVariable("itemId") long itemId) {
        log.debug("received a request to add Comment");
        return itemService.addComment(comment, userId, itemId);
    }

}
