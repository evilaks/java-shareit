package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.client.BookingClient;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.booking.dto.IncomingBookingDto;
import ru.practicum.shareit.util.exception.BadRequestException;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Validated
public class BookingController {

    private final BookingClient bookingClient;


    // POST /bookings
    @PostMapping
    public ResponseEntity<Object> createBooking(@Positive @RequestHeader(value = "X-Sharer-User-Id") Long userId,
                                                @RequestBody IncomingBookingDto incomingBookingDto) {
        log.debug("Creating booking {}, userId={}", incomingBookingDto, userId);
        if (!isValidBooking(incomingBookingDto)) {
            throw new BadRequestException("Invalid booking: " + incomingBookingDto);
        }
        return bookingClient.createBooking(userId, incomingBookingDto);
    }

    // PATCH /bookings/{bookingId}?approved={approved}
    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> updateBookingApproval(@Positive @RequestHeader(value = "X-Sharer-User-Id") Long userId,
                                                        @Positive @PathVariable Long bookingId,
                                                        @RequestParam Boolean approved) {

        log.debug("Update booking approval {}, userId={}", bookingId, userId);
        return bookingClient.updateBookingApproval(userId, bookingId, approved);
    }

    // GET /bookings/{bookingId}
    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBookingById(@Positive @RequestHeader(value = "X-Sharer-User-Id") Long userId,
                                                 @Positive @PathVariable Long bookingId) {
        log.debug("Get booking {}, userId={}", bookingId, userId);
        return bookingClient.getBookingById(userId, bookingId);
    }

    // GET /bookings?state={state}&from={from}&size={size}
    @GetMapping
    public ResponseEntity<Object> getBookingsByState(@Positive @RequestHeader(value = "X-Sharer-User-Id") Long userId,
                                                     @RequestParam(name = "state", defaultValue = "ALL") String stateParam,
                                                     @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                     @Positive @RequestParam(defaultValue = "100") Integer size) {
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new BadRequestException("Unknown state: " + stateParam));
        log.debug("Get booking with state {}, userId={}, from={}, size={}", stateParam, userId, from, size);
        return bookingClient.getBookingsByState(userId, state, from, size);
    }

    // GET /bookings/owner?state={state}&from={from}&size={size}
    @GetMapping("/owner")
    public ResponseEntity<Object> getBookingsByOwnerAndState(@Positive @RequestHeader(value = "X-Sharer-User-Id") Long ownerId,
                                                             @RequestParam(name = "state", defaultValue = "ALL") String stateParam,
                                                             @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                             @Positive @RequestParam(defaultValue = "100") Integer size) {
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new BadRequestException("Unknown state: " + stateParam));
        log.debug("Get booking with state {}, ownerId={}, from={}, size={}", stateParam, ownerId, from, size);
        return bookingClient.getBookingsByOwnerAndState(ownerId, state, from, size);
    }

    private boolean isValidBooking(IncomingBookingDto incomingBookingDto) {
        return incomingBookingDto != null
                && incomingBookingDto.getStart() != null
                && incomingBookingDto.getEnd() != null
                && incomingBookingDto.getItemId() != null
                // starts before ends
                && incomingBookingDto.getStart().isBefore(incomingBookingDto.getEnd())
                // starts after now
                && incomingBookingDto.getStart().isAfter(LocalDateTime.now());
    }
}

