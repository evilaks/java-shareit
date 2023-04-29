package ru.practicum.shareit.booking.service;
import ru.practicum.shareit.booking.dto.IncomingBookingDto;
import ru.practicum.shareit.booking.dto.OutgoingBookingDto;

import java.util.List;


public interface BookingService {

    OutgoingBookingDto createBooking(Long userId, IncomingBookingDto incomingBookingDto);

    OutgoingBookingDto updateBookingApproval(Long bookingId, Long userId, boolean approved);

    OutgoingBookingDto getBookingById(Long userId, Long bookingId);

    List<OutgoingBookingDto> getBookingsByState(Long userId, String state);

    List<OutgoingBookingDto> getBookingsByOwnerAndState(Long ownerId, String state);
}
