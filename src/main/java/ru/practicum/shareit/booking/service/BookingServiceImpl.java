package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
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
            if (booking.getBookerId() == 0) {
                booking.setBookerId(bookingId);
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
    public List<BookingDtoResponse> getBookingListByUser(String state, long userId, String from, String size) {
        validateUser(userId);
        validatePageParameters(from, size);
        int parseFrom = Integer.parseInt(from);
        int parseSize = Integer.parseInt(size);

        try {
            BookingState resultState = Enum.valueOf(BookingState.class, state);
            switch (resultState) {
                case ALL:
                    return mapAndSorted(bookingRepository.findAllByBookerIdAllState(userId,
                            PageRequest.of(parseFrom / parseSize, parseSize)));
                case FUTURE:
                    return mapAndSorted(bookingRepository.getAllByBookerIdForFutureState(userId,
                            PageRequest.of(parseFrom, parseSize)));
                case WAITING:
                    return mapAndSorted(bookingRepository.getAllByBookerIdForWaitingState(userId,
                            PageRequest.of(parseFrom, parseSize)));
                case REJECTED:
                    return mapAndSorted(bookingRepository.getAllByBookerIdForRejectedState(userId,
                            PageRequest.of(parseFrom, parseSize)));
                case CURRENT:
                    return mapAndSorted(bookingRepository.getAllByBookerIdForCurrentState(userId,
                            PageRequest.of(parseFrom, parseSize)));
                case PAST:
                    return mapAndSorted(bookingRepository.getAllByBookerIdForPastState(userId,
                            PageRequest.of(parseFrom, parseSize)));
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
    public List<BookingDtoResponse> getBookingListForThingsUser(String state, long userId, String from, String size) {
        validateUser(userId);
        validatePageParameters(from, size);
        try {
            BookingState resultState = Enum.valueOf(BookingState.class, state);
            List<Long> list;
            List<Booking> bookings = new ArrayList<>();
            switch (resultState) {
                case ALL:
                    list = itemRepository.getListItemIdByUser(userId);
                    for (Long ids : list) {
                        bookings.addAll(bookingRepository.findAllByItemId(ids));
                    }
                    return mapAndSortedWithStart(bookings.stream(), Integer.parseInt(from), Integer.parseInt(size));

                case FUTURE:
                    list = itemRepository.getListItemIdByUser(userId);
                    for (Long ids : list) {
                        bookings.addAll(bookingRepository.findAllByItemId(ids));
                    }
                    return mapAndSortedWithStart(bookings
                                    .stream()
                                    .filter(x -> x.getStatus() == BookingStatus.WAITING
                                            || x.getStatus() == BookingStatus.APPROVED),
                            Integer.parseInt(from), Integer.parseInt(size));
                case WAITING:
                    list = itemRepository.getListItemIdByUser(userId);
                    for (Long ids : list) {
                        bookings.addAll(bookingRepository.findAllByItemId(ids));
                    }
                    return mapAndSortedWithStart(bookings.stream()
                                    .filter(x -> x.getStatus() == BookingStatus.WAITING),
                            Integer.parseInt(from), Integer.parseInt(size));
                case REJECTED:
                    list = itemRepository.getListItemIdByUser(userId);
                    for (Long ids : list) {
                        bookings.addAll(bookingRepository.findAllByItemId(ids));
                    }
                    return mapAndSortedWithStart(bookings.stream()
                                    .filter(x -> x.getStatus() == BookingStatus.REJECTED),
                            Integer.parseInt(from), Integer.parseInt(size));
                case CURRENT:
                    list = itemRepository.getListItemIdByUser(userId);
                    for (Long ids : list) {
                        bookings.addAll(bookingRepository.findAllByItemId(ids));
                    }
                    return mapAndSortedWithStart(bookings.stream()
                                    .filter(x -> x.getStatus() == BookingStatus.REJECTED || x.getStatus() == BookingStatus.APPROVED)
                                    .filter(x -> x.getEnd().isAfter(LocalDateTime.now()) && x.getStart().isBefore(LocalDateTime.now())),
                            Integer.parseInt(from), Integer.parseInt(size));
                case PAST:
                    list = itemRepository.getListItemIdByUser(userId);
                    for (Long ids : list) {
                        bookings.addAll(bookingRepository.findAllByItemId(ids));
                    }
                    return mapAndSortedWithStart(bookings.stream()
                                    .filter(x -> x.getStatus() == BookingStatus.APPROVED)
                                    .filter(x -> x.getEnd().isBefore(LocalDateTime.now()) && x.getStart().isBefore(LocalDateTime.now())),
                            Integer.parseInt(from), Integer.parseInt(size));
                default:
                    throw new ValidationException("Unknown state: UNSUPPORTED_STATUS");
            }
        } catch (RuntimeException e) {
            throw new ValidationException("Unknown state: UNSUPPORTED_STATUS");
        }
    }

    private List<BookingDtoResponse> mapAndSortedWithStart(Stream<Booking> stream, int from, int size) {
        return stream
                .map(x -> mapperBooking.bookingToDtoResponse(x, itemService.getItem(x.getItemId())))
                .sorted(Comparator.comparing(BookingDtoResponse::getStart).reversed())
                .skip(from)
                .limit(size)
                .collect(Collectors.toList());
    }

    private void validateUser(long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("not found user with id: " + userId));
    }

    private void validatePageParameters(String from, String size) {
        int parseFrom;
        int parseSize;
        try {
            parseFrom = Integer.parseInt(from);
            parseSize = Integer.parseInt(size);
        } catch (NumberFormatException e) {
            throw new ValidationException("from or size not a number");
        }
        if (parseFrom < 0 || parseSize <= 0) {
            throw new ValidationException("from can't be < 0 and size can't be <= 0");
        }

    }

}
