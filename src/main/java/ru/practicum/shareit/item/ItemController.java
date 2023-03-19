package ru.practicum.shareit.item;

import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;

@RestController
@RequestMapping("/items")
public class ItemController {

    @PostMapping
    public void addItem(@RequestBody ItemDto itemDto, @RequestHeader("X-Sharer-User-Id") long userId) {

    }

    @PatchMapping("/{itemId}")
    public void updateItem(@PathVariable("itemId") long itemId) {

    }

    @GetMapping("/{itemId}")
    public void getItemInfo(@PathVariable("itemId") long itemId) {

    }

    @GetMapping
    public void getListOfThings() {

    }

    @GetMapping("/search?text={text}")
    public void searchThing(@PathVariable("text") String text) {

    }

}
