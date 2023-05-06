package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.IncomingBookingDto;
import ru.practicum.shareit.booking.dto.OutgoingBookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.storage.UserRepository;

import ru.practicum.shareit.util.exception.NotFoundException;
import ru.practicum.shareit.util.exception.ValidationException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final BookingDtoMapper bookingDtoMapper;
    private final UserService userService;
    private final UserRepository userRepository;
    private final ItemService itemService;
    private final ItemRepository itemRepository;

    @Transactional
    @Override
    public OutgoingBookingDto createBooking(Long userId, IncomingBookingDto incomingBookingDto) {

        // throw 404 if item or user not found
        ItemWithBookingsDto itemDto = itemService.findById(incomingBookingDto.getItemId(), userId);


        if (isValidBooking(incomingBookingDto)) {

            // check availability of item
            if (!itemDto.getAvailable()) {
                throw new ValidationException("Item is not available");
            }

            Item item = itemRepository.findById(incomingBookingDto.getItemId()).orElse(null);
            User booker = userRepository.findById(userId).orElse(null);

            // check if booker is not owner of item
            if (item.getOwner().getId().equals(userId)) {
                throw new NotFoundException("Booker is owner of item");
            }

            // make booking
            Booking booking = bookingDtoMapper.toBooking(incomingBookingDto, item, booker);
            booking.setStatus(BookingStatus.WAITING);
            Booking savedBooking = bookingRepository.save(booking);
            return bookingDtoMapper.toDto(savedBooking);

        } else throw new ValidationException("Invalid booking received");

    }

    @Transactional
    @Override
    public OutgoingBookingDto updateBookingApproval(Long bookingId, Long userId, boolean approved) {

        Booking booking = bookingRepository.findById(bookingId).orElse(null);

        // throw 404 if booking not found
        if (booking == null) {
            throw new NotFoundException("Booking not found");
        }

        // throw 404 if user not found
        userService.findById(userId);

        // check if booking is waiting
        if (!booking.getStatus().equals(BookingStatus.WAITING)) {
            throw new ValidationException("Booking status is not WAITING");
        }

        // check if user is owner of item
        if (booking.getItem().getOwner().getId().equals(userId)) {
            if (approved) {
                booking.setStatus(BookingStatus.APPROVED);
            } else {
                booking.setStatus(BookingStatus.REJECTED);
            }
            Booking savedBooking = bookingRepository.save(booking);
            return bookingDtoMapper.toDto(savedBooking);
        } else throw new NotFoundException("User is not owner of item");
    }

    @Override
    public OutgoingBookingDto getBookingById(Long userId, Long bookingId) {

        Booking booking = bookingRepository.findById(bookingId).orElse(null);

        // throw 404 if booking not found
        if (booking == null) {
            throw new NotFoundException("Booking not found");
        }

        // throw 404 if user is neither owner of item nor booker
        if (!booking.getItem().getOwner().getId().equals(userId)
                && !booking.getBooker().getId().equals(userId)) {
            throw new NotFoundException("Booking not found");
        }

        // return booking
        return bookingDtoMapper.toDto(booking);
    }

    @Override
    public List<OutgoingBookingDto> getBookingsByState(Long userId, String state, Integer from, Integer size) {

        // throw 404 if user not found
        userService.findById(userId);

        // validate from and size
        if (from < 0 || size < 1) {
            throw new ValidationException("Invalid from or size");
        }

        // convert from to page
        int page = from > 0 ? from / size : 0;

        switch (state) {
            case "CURRENT":
                return bookingRepository.findAllByBookerIdAndStartTimeBeforeAndEndTimeAfterOrderByStartTimeDesc(userId,
                        LocalDateTime.now(), LocalDateTime.now(), PageRequest.of(page, size)).stream()
                        .map(bookingDtoMapper::toDto)
                        .collect(Collectors.toList());

            case "PAST":
                return bookingRepository.findAllByBookerIdAndEndTimeBeforeOrderByStartTimeDesc(userId,
                        LocalDateTime.now(), PageRequest.of(page, size))
                        .stream()
                        .map(bookingDtoMapper::toDto)
                        .collect(Collectors.toList());

            case "FUTURE":
                return bookingRepository.findAllByBookerIdAndStartTimeAfterOrderByStartTimeDesc(userId,
                        LocalDateTime.now(), PageRequest.of(page, size))
                        .stream()
                        .map(bookingDtoMapper::toDto)
                        .collect(Collectors.toList());

            case "WAITING":
                return bookingRepository.findAllByBookerIdAndStatusOrderByStartTimeDesc(userId,
                                BookingStatus.WAITING,
                                PageRequest.of(page, size))
                        .stream()
                        .map(bookingDtoMapper::toDto)
                        .collect(Collectors.toList());

            case "REJECTED":
                return bookingRepository.findAllByBookerIdAndStatusOrderByStartTimeDesc(userId,
                                BookingStatus.REJECTED, PageRequest.of(page, size))
                        .stream()
                        .map(bookingDtoMapper::toDto)
                        .collect(Collectors.toList());

            case "ALL":
                return bookingRepository.findAllByBookerIdOrderByStartTimeDesc(userId, PageRequest.of(page, size))
                        .stream()
                        .map(bookingDtoMapper::toDto)
                        .collect(Collectors.toList());

            default:
                throw new ValidationException("Unknown state: " + state);
        }
    }

    @Override
    public List<OutgoingBookingDto> getBookingsByOwnerAndState(Long ownerId, String state, Integer from, Integer size) {

        // throw 404 if user not found
        userService.findById(ownerId);

        // validate from and size
        if (from < 0 || size < 1) {
            throw new ValidationException("Invalid from or size");
        }

        // convert from to page
        int page = from > 0 ? from / size : 0;

        switch (state) {
            case "CURRENT":
                return bookingRepository.findAllByItemOwnerIdAndStartTimeBeforeAndEndTimeAfterOrderByStartTimeDesc(
                                ownerId,
                                LocalDateTime.now(), LocalDateTime.now(), PageRequest.of(page, size)).stream()
                                .map(bookingDtoMapper::toDto)
                                .collect(Collectors.toList());

            case "PAST":
                return bookingRepository.findAllByItemOwnerIdAndEndTimeBeforeOrderByStartTimeDesc(ownerId,
                                LocalDateTime.now(), PageRequest.of(page, size))
                        .stream()
                        .map(bookingDtoMapper::toDto)
                        .collect(Collectors.toList());

            case "FUTURE":
                return bookingRepository.findAllByItemOwnerIdAndStartTimeAfterOrderByStartTimeDesc(ownerId,
                                LocalDateTime.now(), PageRequest.of(page, size))
                        .stream()
                        .map(bookingDtoMapper::toDto)
                        .collect(Collectors.toList());

            case "WAITING":
                return bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartTimeDesc(ownerId,
                        BookingStatus.WAITING, PageRequest.of(page, size))
                        .stream()
                        .map(bookingDtoMapper::toDto)
                        .collect(Collectors.toList());

            case "REJECTED":
                return bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartTimeDesc(ownerId,
                        BookingStatus.REJECTED, PageRequest.of(page, size))
                        .stream()
                        .map(bookingDtoMapper::toDto)
                        .collect(Collectors.toList());

            case "ALL":
                return bookingRepository.findAllByItemOwnerIdOrderByStartTimeDesc(ownerId, PageRequest.of(page, size))
                        .stream()
                        .map(bookingDtoMapper::toDto)
                        .collect(Collectors.toList());

            default:
                throw new ValidationException("Unknown state: " + state);
        }

    }

    private boolean isValidBooking(IncomingBookingDto incomingBookingDto) {
        return incomingBookingDto.getStart() != null
                && incomingBookingDto.getEnd() != null
                && incomingBookingDto.getItemId() != null
                // starts before ends
                && incomingBookingDto.getStart().isBefore(incomingBookingDto.getEnd())
                // starts after now
                && incomingBookingDto.getStart().isAfter(LocalDateTime.now());
    }
}
