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
        itemRequest = ItemRequest.builder()
                .id(1)
                .description("des")
                .requestor(1)
                .created(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
                .build();
        itemRequestDto = ItemRequestDto.builder()
                .id(1)
                .description("des")
                .created(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
                .items(null)
                .build();
    }

    @Test
    void toDto() {
        MapperItemRequest.toDto(itemRequest);

        assertEquals(itemRequestDto, MapperItemRequest.toDto(itemRequest));
    }
}