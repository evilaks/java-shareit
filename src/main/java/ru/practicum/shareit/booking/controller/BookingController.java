package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.IncomingBookingDto;
import ru.practicum.shareit.booking.dto.OutgoingBookingDto;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;


@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;


    // POST /bookings
    @PostMapping
    public ResponseEntity<OutgoingBookingDto> createBooking(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
                                                            @RequestBody IncomingBookingDto incomingBookingDto) {
        return ResponseEntity.ok().body(bookingService.createBooking(userId, incomingBookingDto));
    }

    // PATCH /bookings/{bookingId}?approved={approved}
    @PatchMapping("/{bookingId}")
    public ResponseEntity<OutgoingBookingDto> updateBookingApproval(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
                                                                    @PathVariable Long bookingId,
                                                                    @RequestParam Boolean approved) {
        OutgoingBookingDto outgoingBookingDto = bookingService.updateBookingApproval(bookingId, userId, approved);
        return ResponseEntity.ok().body(outgoingBookingDto);
    }

    // GET /bookings/{bookingId}
    @GetMapping("/{bookingId}")
    public ResponseEntity<OutgoingBookingDto> getBookingById(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
                                                             @PathVariable Long bookingId) {
        OutgoingBookingDto outgoingBookingDto = bookingService.getBookingById(userId, bookingId);
        return ResponseEntity.ok().body(outgoingBookingDto);
    }

    // GET /bookings?state={state}
    @GetMapping
    public ResponseEntity<List<OutgoingBookingDto>> getBookingsByState(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
                                                                       @RequestParam(defaultValue = "ALL") String state) {
        List<OutgoingBookingDto> outgoingBookingDtos = bookingService.getBookingsByState(userId, state);
        return ResponseEntity.ok().body(outgoingBookingDtos);
    }

    // GET /bookings/owner?state={state}
    @GetMapping("/owner")
    public ResponseEntity<List<OutgoingBookingDto>> getBookingsByOwnerAndState(@RequestHeader(value = "X-Sharer-User-Id") Long ownerId,
                                                                               @RequestParam(defaultValue = "ALL") String state) {
        List<OutgoingBookingDto> outgoingBookingDtos = bookingService.getBookingsByOwnerAndState(ownerId, state);
        return ResponseEntity.ok().body(outgoingBookingDtos);
    }
}

