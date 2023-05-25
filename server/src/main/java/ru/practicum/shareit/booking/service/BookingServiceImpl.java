package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.mapper.MapperBooking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@RequiredArgsConstructor
@Service
public class BookingServiceImpl implements BookingService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemService itemService;
    private final BookingRepository bookingRepository;
    private final MapperBooking mapperBooking;

    @Override
    public BookingDtoResponse addBooking(BookingDto bookingDto, long userId) {
        validateBooking(bookingDto, userId);
        bookingDto.setBooker(userId);
        bookingDto.setStatus(BookingStatus.WAITING);
        Booking booking = bookingRepository.save(MapperBooking.dtoToBooking(bookingDto));
        return mapperBooking.bookingToDtoResponse(booking, itemService.getItem(booking.getItemId()));
    }

    private void validateBooking(BookingDto bookingDto, long userId) {
        validateUser(userId);
        itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() -> new NotFoundException("not found item with id: " + bookingDto.getItemId()));
        if (itemRepository.getById(bookingDto.getItemId()).getOwner() == userId) {
            throw new NotFoundException("user must be the owner");
        }
        if (!itemRepository.getById(bookingDto.getItemId()).getAvailable()) {
            throw new ValidationException("booking for item with id: " + bookingDto.getItemId() + " not available");
        }
        if (bookingDto.getEnd().isEqual(bookingDto.getStart()) || bookingDto.getEnd().isBefore(bookingDto.getStart())) {
            throw new ValidationException("end must not be earlier than the beginning or equal to");
        }
    }

    @Transactional
    @Override
    public BookingDtoResponse processTheRequest(long userId, long bookingId, String approved) {
        Booking booking = getBooking(bookingId);
        boolean app = Boolean.parseBoolean(approved);
        if (booking.getStatus() == BookingStatus.APPROVED) {
            throw new ValidationException("status has already been approved");
        }
        if (itemService.getItem(booking.getItemId()).getOwner() == userId) {
            if (app) {
                booking.setStatus(BookingStatus.APPROVED);
            } else {
                booking.setStatus(BookingStatus.REJECTED);
            }
            return mapperBooking.bookingToDtoResponse(bookingRepository.save(booking),
                    itemService.getItem(booking.getItemId()));

        } else {
            throw new NotFoundException("booking request can only be performed by the owner of the item");
        }
    }

    @Transactional(readOnly = true)
    @Override
    public BookingDtoResponse getBookingDtoResponse(long bookingId, long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("not found booking with id: " + bookingId));
        if (booking.getBookerId() == userId || itemService.getItem(booking.getItemId()).getOwner() == userId) {
            return mapperBooking.bookingToDtoResponse(booking, itemService.getItem(booking.getItemId()));
        } else {
            throw new NotFoundException("It can be performed either by the booking author or by the owner of the item");
        }
    }

    @Transactional(readOnly = true)
    @Override
    public Booking getBooking(long bookingId) {
        return bookingRepository.getById(bookingId);
    }

    @Transactional(readOnly = true)
    @Override
    public List<BookingDtoResponse> getBookingListByUser(String state, long userId, Pageable pageable) {
        validateUser(userId);

        try {
            BookingState resultState = Enum.valueOf(BookingState.class, state);
            switch (resultState) {
                case ALL:
                    return mapAndSorted(bookingRepository.findAllByBookerIdOrderByEndDesc(userId,
                            PageRequest.of(pageable.getPageNumber() / pageable.getPageSize(), pageable.getPageSize())));
                case FUTURE:
                    return mapAndSorted(bookingRepository.getAllByBookerIdForFutureState(userId,
                            pageable));
                case WAITING:
                    return mapAndSorted(bookingRepository.getAllByBookerIdForWaitingState(userId,
                            pageable));
                case REJECTED:
                    return mapAndSorted(bookingRepository.getAllByBookerIdForRejectedState(userId,
                            pageable));
                case CURRENT:
                    return mapAndSorted(bookingRepository.getAllByBookerIdForCurrentState(userId,
                            pageable));
                case PAST:
                    return mapAndSorted(bookingRepository.getAllByBookerIdForPastState(userId,
                            pageable));
                default:
                    throw new ValidationException("Unknown state: UNSUPPORTED_STATUS");
            }
        } catch (RuntimeException e) {
            throw new ValidationException("Unknown state: UNSUPPORTED_STATUS");
        }
    }

    private List<BookingDtoResponse> mapAndSorted(Page<Booking> page) {
        return page
                .stream()
                .map(x -> mapperBooking.bookingToDtoResponse(x, itemService.getItem(x.getItemId())))
                .sorted(Comparator.comparing(BookingDtoResponse::getStart).reversed())
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public List<BookingDtoResponse> getBookingListForThingsUser(String state, long userId, Pageable pageable) {
        validateUser(userId);
        try {
            BookingState resultState = Enum.valueOf(BookingState.class, state);

            switch (resultState) {
                case ALL:
                    return mapAndSortedForThingsUser(getPage(userId, pageable).getContent().stream(), userId);

                case FUTURE:
                    return mapAndSortedForThingsUser(getPage(userId, pageable).getContent()
                            .stream()
                            .filter(x -> x.getStatus() == BookingStatus.WAITING
                                    || x.getStatus() == BookingStatus.APPROVED), userId);

                case WAITING:
                    return mapAndSortedForThingsUser(getPage(userId, pageable).getContent()
                            .stream()
                            .filter(x -> x.getStatus() == BookingStatus.WAITING), userId);

                case REJECTED:
                    return mapAndSortedForThingsUser(getPage(userId, pageable).getContent()
                            .stream()
                            .filter(x -> x.getStatus() == BookingStatus.REJECTED), userId);

                case CURRENT:
                    return mapAndSortedForThingsUser(getPage(userId, pageable).getContent()
                            .stream()
                            .filter(x -> x.getStatus() == BookingStatus.REJECTED || x.getStatus() == BookingStatus.APPROVED)
                            .filter(x -> x.getEnd().isAfter(LocalDateTime.now()) && x.getStart().isBefore(LocalDateTime.now())), userId);

                case PAST:
                    return mapAndSortedForThingsUser(getPage(userId, pageable).getContent()
                            .stream()
                            .filter(x -> x.getStatus() == BookingStatus.APPROVED)
                            .filter(x -> x.getEnd().isBefore(LocalDateTime.now()) && x.getStart().isBefore(LocalDateTime.now())), userId);

                default:
                    throw new ValidationException("Unknown state: UNSUPPORTED_STATUS");
            }
        } catch (RuntimeException e) {
            throw new ValidationException("Unknown state: UNSUPPORTED_STATUS");
        }
    }

    private Page<Booking> getPage(long userId, Pageable pageable) {
        List<Long> list = itemRepository.getListItemIdByUser(userId);

        return bookingRepository.findAllByItemIdInOrderByStartDesc(list, pageable);
    }

    private List<BookingDtoResponse> mapAndSortedForThingsUser(Stream<Booking> stream, long userId) {
        Map<Long, Item> items = itemRepository.findAllByOwner(userId)
                .stream()
                .collect(Collectors.toMap(Item::getId, item -> item));
        return stream
                .map(x -> mapperBooking.bookingToDtoResponse(x, items.get(x.getItemId())))
                .collect(Collectors.toList());
    }

    private void validateUser(long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("not found user with id: " + userId));
    }

}
