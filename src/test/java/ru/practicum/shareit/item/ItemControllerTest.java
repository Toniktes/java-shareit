package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;
import ru.practicum.shareit.item.service.ItemService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@ExtendWith(MockitoExtension.class)
class ItemControllerTest {
    @Mock
    private ItemService itemService;
    @InjectMocks
    private ItemController itemController;
    private ObjectMapper mapper;
    private MockMvc mockMvc;
    private ItemDto itemDto;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(itemController)
                .build();

        mapper = new ObjectMapper();

        itemDto = new ItemDto(
                1,
                "name",
                "description",
                true,
                0L
        );
    }

    @SneakyThrows
    @Test
    void addItem_whenInvoked_thenResponseStatusOkWithCreatedUserInBody() {
        Mockito.when(itemService.addItem(Mockito.any(), Mockito.anyLong()))
                .thenReturn(itemDto);

        mockMvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())));
    }

    @SneakyThrows
    @Test
    void updateItem() {
        Mockito.when(itemService.updateItem(Mockito.any(), Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(itemDto);

        mockMvc.perform(patch("/items/{itemId}", itemDto.getId())
                        .content(mapper.writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())));
    }

    @SneakyThrows
    @Test
    void getItemInfo() {
        ItemDtoWithBooking itemInfo = new ItemDtoWithBooking(
                1,
                "name",
                "description",
                true,
                null,
                null,
                null
        );
        Mockito.when(itemService.getItemDtoWithBooking(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(itemInfo);

        mockMvc.perform(get("/items/{itemId}", itemInfo.getId())
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(itemService.getItemDtoWithBooking(itemInfo.getId(), 1))));
    }

    @SneakyThrows
    @Test
    void getListOfThings() {
        ItemDtoWithBooking itemInfo = new ItemDtoWithBooking(
                1,
                "name",
                "description",
                true,
                null,
                null,
                null
        );
        List<ItemDtoWithBooking> listItems = List.of(itemInfo);
        Mockito.when(itemService.getListOfThings(Mockito.anyLong(), Mockito.anyString(), Mockito.anyString()))
                .thenReturn(listItems);

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(itemController.getListOfThings(1, "0", "20"))));
    }

    @SneakyThrows
    @Test
    void searchThing() {
        List<ItemDto> itemDtos = List.of(itemDto);
        Mockito.when(itemService.getThingsForSearch(Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
                .thenReturn(itemDtos);

        mockMvc.perform(get("/items/search")
                        .param("text", "text"))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(itemController.searchThing("text", "0", "20"))));
    }

    @SneakyThrows
    @Test
    void addComment() {
        Comment comment = new Comment(
                1,
                "text",
                1,
                "author",
                null
        );
        Mockito.when(itemService.addComment(Mockito.any(), Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(comment);

        mockMvc.perform(post("/items/{itemId}/comment", 1)
                        .content(mapper.writeValueAsString(comment))
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(itemService.addComment(comment, 1, comment.getItem()))));
    }
}