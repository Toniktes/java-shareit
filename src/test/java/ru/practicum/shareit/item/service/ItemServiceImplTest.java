package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.Comment;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;
import ru.practicum.shareit.item.mapper.MapperItem;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserService userService;
    @Mock
    private MapperItem mapperItem;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private BookingRepository bookingRepository;
    private UserRepository userRepository;
    @InjectMocks
    @Spy
    private ItemServiceImpl itemService;

    private ItemDto itemDto;

    @BeforeEach
    void setUp() {
        itemDto = new ItemDto(
                1,
                "name",
                "des",
                true,
                1
        );
    }

    @Test
    void addItem_whenValid_thenReturnItem() {
        doNothing().when(itemService).validate(Mockito.any(), Mockito.anyLong());
        when(itemRepository.save(Mockito.any()))
                .thenReturn(MapperItem.dtoToItem(itemDto, 1));
        ItemDto actualItem = itemService.addItem(itemDto, 1);

        assertEquals(itemDto, actualItem);
        verify(itemRepository).save(MapperItem.dtoToItem(itemDto, 1));
    }

    @Test
    void addItem_whenNotValid_thenReturnItem() {
        doNothing().when(itemService).validate(Mockito.any(), Mockito.anyLong());
        when(itemRepository.save(Mockito.any()))
                .thenThrow(NotFoundException.class);

        assertThrows(NotFoundException.class, () -> itemService.addItem(itemDto, 1));
    }

    @Test
    void updateItem() {
        doNothing().when(itemService).validateUpdate(Mockito.any(), Mockito.anyLong(), Mockito.anyLong());
        when(itemRepository.save(Mockito.any()))
                .thenReturn(MapperItem.dtoToItem(itemDto, 1));
        ItemDto actualItem = itemService.updateItem(itemDto, 1, 1);

        assertEquals(itemDto, actualItem);
        verify(itemRepository, times(1)).save(MapperItem.dtoToItem(itemDto, 1));

    }

    @Test
    void getItemDto() {
        when(itemRepository.getById(Mockito.anyLong())).thenReturn(MapperItem.dtoToItem(itemDto, 1));

        ItemDto actualItem = itemService.getItemDto(itemDto.getId());

        assertEquals(itemDto, actualItem);
    }

    @Test
    void getItemDtoWithBooking() {
        ItemDtoWithBooking item = new ItemDtoWithBooking(
                1,
                "name",
                "des",
                true,
                null,
                null,
                null
        );
        when(mapperItem.itemToDtoWithBooking(Mockito.any())).thenReturn(item);
        when(itemRepository.getById(Mockito.anyLong())).thenReturn(MapperItem.dtoToItem(itemDto, 1));

        ItemDtoWithBooking actualItem = itemService.getItemDtoWithBooking(item.getId(), 1);

        assertEquals(item, actualItem);
    }

    @Test
    void getListOfThings() {
        ItemDtoWithBooking item = new ItemDtoWithBooking(
                1,
                "name",
                "des",
                true,
                null,
                null,
                null
        );
        List<Item> itemList = List.of(MapperItem.dtoToItem(itemDto, 1));
        List<ItemDtoWithBooking> listDto = itemList.stream().map(mapperItem::itemToDtoWithBooking).collect(Collectors.toList());
        Page<Item> page = new PageImpl<>(itemList);
        when(itemRepository.findAllByOwner(Mockito.anyLong(), Mockito.any()))
                .thenReturn(page);

        List<ItemDtoWithBooking> actualItemList = itemService.getListOfThings(item.getId(), "0", "20");

        assertEquals(listDto, actualItemList);
    }

    @Test
    void getThingsForSearch() {
        List<Item> itemList = List.of(MapperItem.dtoToItem(itemDto, 1));
        List<ItemDto> listDto = itemList.stream().map(MapperItem::itemToDto).collect(Collectors.toList());
        Page<Item> page = new PageImpl<>(itemList);
        when(itemRepository.findAll(PageRequest.of(0, 20))).thenReturn(page);

        List<ItemDto> actualItemList = itemService.getThingsForSearch("name", "0", "20");

        assertEquals(listDto, actualItemList);
    }

    @Test
    void getThingsForSearch_whenEmptyText_thenReturnEmptyList() {
        List<ItemDto> actualItemList = itemService.getThingsForSearch("", "0", "20");

        assertEquals(List.of(), actualItemList);
    }

    @Test
    void getItem() {
        when(itemRepository.getById(Mockito.anyLong())).thenReturn(MapperItem.dtoToItem(itemDto, 1));

        Item actualItem = itemService.getItem(itemDto.getId());

        assertEquals(actualItem, MapperItem.dtoToItem(itemDto, 1));
    }

    @Test
    void addComment() {
        Comment comment = new Comment(
                1,
                "text",
                1,
                "author",
                null
        );
        Booking booking = new Booking(1,
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().minusHours(1),
                1,
                1,
                BookingStatus.APPROVED);
        UserDto userDto = new UserDto(
                1,
                "author",
                "yan@email"
        );
        List<Booking> listBooking = List.of(booking);
        when(bookingRepository.findByItemIdAndBookerIdAndStatus(anyLong(), anyLong(), any())).thenReturn(listBooking);
        when(commentRepository.save(Mockito.any())).thenReturn(comment);
        when(userService.getUser(Mockito.anyLong())).thenReturn(userDto);

        Comment actualComment = itemService.addComment(comment, 1, 1);

        assertEquals(comment, actualComment);
    }
}