package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
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
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;


@RequiredArgsConstructor
@Service
public class BookingServiceImpl implements BookingService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemService itemService;
    private final BookingRepository bookingRepository;
    private final MapperBooking mapperBooking;

    @Transactional
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
            throw new NotFoundException("kk");
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
            if (booking.getBooker() == 0) {
                booking.setBooker(bookingId);
            }
            return mapperBooking.bookingToDtoResponse(bookingRepository.save(booking),
                    itemService.getItem(booking.getItemId()));

        } else {
            throw new NotFoundException("booking request can only be performed by the owner of the item");
        }
    }

    @Transactional(readOnly = true)
    @Override
    public BookingDtoResponse getBooking(long bookingId, long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("not found booking with id: " + bookingId));
        if (booking.getBooker() == userId || itemService.getItem(booking.getItemId()).getOwner() == userId) {
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
    public List<BookingDtoResponse> getBookingListByUser(String state, long userId) {
        validateUser(userId);
        try {
            BookingState resultState = Enum.valueOf(BookingState.class, state);
            switch (resultState) {
                case ALL:
                    return bookingRepository.findAllByBooker(userId)
                            .stream()
                            .map(x -> mapperBooking.bookingToDtoResponse(x, itemService.getItem(x.getItemId())))
                            .sorted(Comparator.comparing(BookingDtoResponse::getStart).reversed())
                            .collect(Collectors.toList());
                case FUTURE:
                    return bookingRepository.findAllByBooker(userId)
                            .stream()
                            .filter(x -> x.getStatus() == BookingStatus.WAITING || x.getStatus() == BookingStatus.APPROVED)
                            .map(x -> mapperBooking.bookingToDtoResponse(x, itemService.getItem(x.getItemId())))
                            .sorted(Comparator.comparing(BookingDtoResponse::getStart).reversed())
                            .collect(Collectors.toList());
                default:
                    throw new ValidationException("Unknown state: UNSUPPORTED_STATUS");
            }
        } catch (RuntimeException e) {
            throw new ValidationException("Unknown state: UNSUPPORTED_STATUS");
        }
    }

    @Transactional(readOnly = true)
    @Override
    public List<BookingDtoResponse> getBookingListForThingsUser(String state, long userId) {
        validateUser(userId);
        try {
            BookingState resultState = Enum.valueOf(BookingState.class, state);
            List<Long> list;
            List<Booking> bookings;
            switch (resultState) {
                case ALL:
                    list = itemService.getListOfThings(userId)
                            .stream()
                            .map(ItemDto::getId)
                            .collect(Collectors.toList());
                    bookings = new ArrayList<>();
                    for (Long ids : list) {
                        bookings.addAll(bookingRepository.findAllByItemId(ids));
                    }
                    return bookings.stream()
                            .map(x -> mapperBooking.bookingToDtoResponse(x, itemService.getItem(x.getItemId())))
                            .sorted(Comparator.comparing(BookingDtoResponse::getStart).reversed())
                            .collect(Collectors.toList());
                case FUTURE:
                    list = itemService.getListOfThings(userId)
                            .stream()
                            .map(ItemDto::getId)
                            .collect(Collectors.toList());
                    bookings = new ArrayList<>();
                    for (Long ids : list) {
                        bookings.addAll(bookingRepository.findAllByItemId(ids));
                    }
                    return bookings.stream()
                            .filter(x -> x.getStatus() == BookingStatus.WAITING || x.getStatus() == BookingStatus.APPROVED)
                            .map(x -> mapperBooking.bookingToDtoResponse(x, itemService.getItem(x.getItemId())))
                            .sorted(Comparator.comparing(BookingDtoResponse::getStart).reversed())
                            .collect(Collectors.toList());
                default:
                    throw new ValidationException("Unknown state: UNSUPPORTED_STATUS");
            }
        } catch (RuntimeException e) {
            throw new ValidationException("Unknown state: UNSUPPORTED_STATUS");
        }
    }

    private void validateUser(long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("not found user with id: " + userId));
    }

}