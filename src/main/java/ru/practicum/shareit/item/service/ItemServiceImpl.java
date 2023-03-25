package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.MapperItem;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;
    private final UserService userService;
    private long generatorId = 0;

    @Override
    public Item addItem(ItemDto itemDto, long userId) {
        validate(itemDto, userId);
        return itemStorage.addItem(MapperItem.dtoToItem(itemDto, userId));
    }

    @Override
    public ItemDto updateItem(ItemDto itemDto, long itemId, long userId) {
        validateUpdate(itemDto, itemId, userId);
        return MapperItem.itemToDto(itemStorage.updateItem(MapperItem.dtoToItem(itemDto, userId)));
    }

    public void validateUpdate(ItemDto itemDto, long itemId, long userId) {
        if (itemDto.getName() == null) {
            itemDto.setName(getItemDto(itemId).getName());
        }
        if (itemDto.getDescription() == null) {
            itemDto.setDescription(getItemDto(itemId).getDescription());
        }
        if (itemDto.getAvailable() == null) {
            itemDto.setAvailable(getItemDto(itemId).getAvailable());
        }
        if (itemDto.getId() == 0) {
            itemDto.setId(itemId);
        }
        if (itemStorage.getItem(itemId).getOwner() != userId) {
            throw new NotFoundException("you can't change the owner of a thing");
        }
    }

    @Override
    public ItemDto getItemDto(long itemId) {
        return MapperItem.itemToDto(itemStorage.getItem(itemId));
    }

    @Override
    public List<ItemDto> getListOfThings(long userId) {
        return itemStorage.getAllItems()
                .stream()
                .filter(x -> x.getOwner() == userId)
                .map(MapperItem::itemToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> getThingsForSearch(String text) {
        if (text == null || text.isEmpty()) {
            return List.of();
        }
        return itemStorage.getAllItems()
                .stream()
                .filter(x -> x.getDescription().toLowerCase().contains(text.toLowerCase()) || x.getName().toLowerCase().contains(text.toLowerCase()))
                .filter(Item::getAvailable)
                .map(MapperItem::itemToDto)
                .collect(Collectors.toList());
    }

    private void generateId(ItemDto itemDto) {
        if (itemDto.getId() == 0) {
            itemDto.setId(++generatorId);
        }
    }

    private void validate(ItemDto itemDto, long userId) {
        if (userService.getUser(userId) == null) {
            throw new NotFoundException("user not found");
        }
        if (itemDto.getAvailable() == null || itemDto.getName() == null || itemDto.getDescription() == null) {
            throw new ValidationException("without parameter: available or name or description");
        }
        if (itemDto.getName().isBlank() || itemDto.getDescription().isBlank()) {
            throw new ValidationException("parameter name or description is empty");
        }
        generateId(itemDto);
    }
}
