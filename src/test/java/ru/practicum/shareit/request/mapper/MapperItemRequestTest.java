package ru.practicum.shareit.request.mapper;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class MapperItemRequestTest {
    private ItemRequest itemRequest;
    private ItemRequestDto itemRequestDto;

    @BeforeEach
    public void setUp() {
        itemRequest = new ItemRequest();
        itemRequest.setId(1);
        itemRequest.setDescription("des");
        itemRequest.setRequestor(1);
        itemRequest.setCreated(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));

        itemRequestDto = new ItemRequestDto();
        itemRequestDto.setId(1);
        itemRequestDto.setDescription("des");
        itemRequestDto.setCreated(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        itemRequestDto.setItems(null);
    }

    @Test
    void toDto() {
        MapperItemRequest.toDto(itemRequest);

        assertEquals(itemRequestDto, MapperItemRequest.toDto(itemRequest));
    }
}