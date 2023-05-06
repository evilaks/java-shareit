package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.IncomingBookingDto;
import ru.practicum.shareit.booking.dto.OutgoingBookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.storage.UserRepository;
import ru.practicum.shareit.util.exception.NotFoundException;
import ru.practicum.shareit.util.exception.ValidationException;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.List;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTests {

    @InjectMocks
    private BookingServiceImpl bookingService;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private BookingDtoMapper bookingDtoMapper;

    @Mock
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemService itemService;

    @Mock
    private ItemRepository itemRepository;

    private User user;

    private User booker;

    private UserDto userDto;
    private Item item;
    private IncomingBookingDto incomingBookingDto;
    private Booking booking;
    private OutgoingBookingDto outgoingBookingDto;
    private ItemWithBookingsDto itemDto;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);

        userDto = UserDto.builder()
                .id(1L)
                .build();

        booker = new User();
        booker.setId(2L);

        item = new Item();
        item.setId(2L);
        item.setOwner(user);

        incomingBookingDto = IncomingBookingDto.builder()
                .itemId(2L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .bookerId(2L)
                .build();

        booking = new Booking();
        booking.setId(3L);
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.WAITING);

        outgoingBookingDto = OutgoingBookingDto.builder()
                .id(3L)
                .build();

        itemDto = ItemWithBookingsDto
                .builder()
                .id(2L)
                .available(true)
                .build();

    }

    @Test
    void testCreateBooking_success() {
        when(itemService.findById(incomingBookingDto.getItemId(), booker.getId())).thenReturn(itemDto);
        when(itemRepository.findById(incomingBookingDto.getItemId())).thenReturn(Optional.of(item));
        when(userRepository.findById(booker.getId())).thenReturn(Optional.of(booker));
        when(bookingDtoMapper.toBooking(any(IncomingBookingDto.class), any(Item.class), any(User.class))).thenReturn(booking);
        when(bookingRepository.save(booking)).thenReturn(booking);
        when(bookingDtoMapper.toDto(booking)).thenReturn(outgoingBookingDto);

        OutgoingBookingDto result = bookingService.createBooking(booker.getId(), incomingBookingDto);

        assertEquals(outgoingBookingDto.getId(), result.getId());
    }

    @Test
    void testCreateBooking_itemNotAvailable() {
        itemDto.setAvailable(false);
        when(itemService.findById(incomingBookingDto.getItemId(), user.getId())).thenReturn(itemDto);

        assertThrows(ValidationException.class, () -> bookingService.createBooking(user.getId(), incomingBookingDto));
    }

    @Test
    void testCreateBooking_bookerIsOwnerOfItem() {

        itemDto = ItemWithBookingsDto.builder()
                .id(item.getId())
                .available(true)
                .build();

        when(itemService.findById(incomingBookingDto.getItemId(), user.getId())).thenReturn(itemDto);
        when(itemRepository.findById(incomingBookingDto.getItemId())).thenReturn(Optional.of(item));
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        // Set the itemId in the incomingBookingDto to the itemOwnedByBooker's id
        incomingBookingDto.setItemId(item.getId());

        assertThrows(NotFoundException.class, () -> bookingService.createBooking(user.getId(), incomingBookingDto));
    }

    @Test
    void testUpdateBookingApproval_success() {
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        when(userService.findById(user.getId())).thenReturn(userDto);
        when(bookingRepository.save(booking)).thenReturn(booking);
        when(bookingDtoMapper.toDto(booking)).thenReturn(outgoingBookingDto);

        OutgoingBookingDto result = bookingService.updateBookingApproval(booking.getId(), user.getId(), true);

        assertEquals(outgoingBookingDto.getId(), result.getId());
    }

    @Test
    void testUpdateBookingApproval_bookingNotFound() {
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.updateBookingApproval(booking.getId(), user.getId(), true));
    }

    @Test
    void testUpdateBookingApproval_userNotFound() {
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        when(userService.findById(user.getId())).thenThrow(new NotFoundException("User not found"));

        assertThrows(NotFoundException.class, () -> bookingService.updateBookingApproval(booking.getId(), user.getId(), true));
    }

    @Test
    void testGetBookingById_success() {
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        when(bookingDtoMapper.toDto(booking)).thenReturn(outgoingBookingDto);

        OutgoingBookingDto result = bookingService.getBookingById(user.getId(), booking.getId());

        assertEquals(outgoingBookingDto.getId(), result.getId());
    }

    @Test
    void testGetBookingById_bookingNotFound() {
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.getBookingById(user.getId(), booking.getId()));
    }

    @Test
    void testGetBookingById_userNotOwnerNorBooker() {
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));

        assertThrows(NotFoundException.class, () -> bookingService.getBookingById(-1L, booking.getId()));
    }

    @Test
    void testGetBookingsByState_success() {
        List<Booking> bookings = Collections.singletonList(booking);
        when(userService.findById(user.getId())).thenReturn(userDto);
        when(bookingRepository.findAllByBookerIdOrderByStartTimeDesc(any(), any())).thenReturn(bookings);
        when(bookingDtoMapper.toDto(booking)).thenReturn(outgoingBookingDto);

        List<OutgoingBookingDto> results = bookingService.getBookingsByState(user.getId(), "ALL", 0, 1);

        assertEquals(1, results.size());
        assertEquals(outgoingBookingDto.getId(), results.get(0).getId());
    }

    @Test
    void testGetBookingsByState_current() {
        List<Booking> bookings = Collections.singletonList(booking);
        when(userService.findById(user.getId())).thenReturn(userDto);
        when(bookingRepository.findAllByBookerIdAndStartTimeBeforeAndEndTimeAfterOrderByStartTimeDesc(any(), any(), any(), any())).thenReturn(bookings);
        when(bookingDtoMapper.toDto(booking)).thenReturn(outgoingBookingDto);

        List<OutgoingBookingDto> results = bookingService.getBookingsByState(user.getId(), "CURRENT", 0, 1);

        assertEquals(1, results.size());
        assertEquals(outgoingBookingDto.getId(), results.get(0).getId());
    }

    @Test
    void testGetBookingsByState_past() {
        List<Booking> bookings = Collections.singletonList(booking);
        when(userService.findById(user.getId())).thenReturn(userDto);
        when(bookingRepository.findAllByBookerIdAndEndTimeBeforeOrderByStartTimeDesc(any(), any(), any())).thenReturn(bookings);
        when(bookingDtoMapper.toDto(booking)).thenReturn(outgoingBookingDto);

        List<OutgoingBookingDto> results = bookingService.getBookingsByState(user.getId(), "PAST", 0, 1);

        assertEquals(1, results.size());
        assertEquals(outgoingBookingDto.getId(), results.get(0).getId());
    }

    @Test
    void testGetBookingsByState_future() {
        List<Booking> bookings = Collections.singletonList(booking);
        when(userService.findById(user.getId())).thenReturn(userDto);
        when(bookingRepository.findAllByBookerIdAndStartTimeAfterOrderByStartTimeDesc(any(), any(), any())).thenReturn(bookings);
        when(bookingDtoMapper.toDto(booking)).thenReturn(outgoingBookingDto);

        List<OutgoingBookingDto> results = bookingService.getBookingsByState(user.getId(), "FUTURE", 0, 1);

        assertEquals(1, results.size());
        assertEquals(outgoingBookingDto.getId(), results.get(0).getId());
    }

    @Test
    void testGetBookingsByState_waiting() {
        List<Booking> bookings = Collections.singletonList(booking);
        when(userService.findById(user.getId())).thenReturn(userDto);
        when(bookingRepository.findAllByBookerIdAndStatusOrderByStartTimeDesc(any(), any(), any())).thenReturn(bookings);
        when(bookingDtoMapper.toDto(booking)).thenReturn(outgoingBookingDto);

        List<OutgoingBookingDto> results = bookingService.getBookingsByState(user.getId(), "WAITING", 0, 1);

        assertEquals(1, results.size());
        assertEquals(outgoingBookingDto.getId(), results.get(0).getId());
    }

    @Test
    void testGetBookingsByState_rejected() {
        List<Booking> bookings = Collections.singletonList(booking);
        when(userService.findById(user.getId())).thenReturn(userDto);
        when(bookingRepository.findAllByBookerIdAndStatusOrderByStartTimeDesc(any(), any(), any())).thenReturn(bookings);
        when(bookingDtoMapper.toDto(booking)).thenReturn(outgoingBookingDto);

        List<OutgoingBookingDto> results = bookingService.getBookingsByState(user.getId(), "REJECTED", 0, 1);

        assertEquals(1, results.size());
        assertEquals(outgoingBookingDto.getId(), results.get(0).getId());
    }

    @Test
    void testGetBookingsByState_unknownState() {
        assertThrows(ValidationException.class, () -> bookingService.getBookingsByState(user.getId(), "UNKNOWN", 0, 1));
    }

    @Test
    void testGetBookingsByOwnerAndState_success() {
        List<Booking> bookings = Collections.singletonList(booking);
        when(userService.findById(user.getId())).thenReturn(userDto);
        when(bookingRepository.findAllByItemOwnerIdOrderByStartTimeDesc(any(), any())).thenReturn(bookings);
        when(bookingDtoMapper.toDto(booking)).thenReturn(outgoingBookingDto);

        List<OutgoingBookingDto> results = bookingService.getBookingsByOwnerAndState(user.getId(), "ALL", 0, 1);

        assertEquals(1, results.size());
        assertEquals(outgoingBookingDto.getId(), results.get(0).getId());
    }

    @Test
    void testGetBookingsByOwnerAndState_past() {
        List<Booking> bookings = Collections.singletonList(booking);
        when(userService.findById(user.getId())).thenReturn(userDto);
        when(bookingRepository.findAllByItemOwnerIdAndEndTimeBeforeOrderByStartTimeDesc(any(), any(), any())).thenReturn(bookings);
        when(bookingDtoMapper.toDto(booking)).thenReturn(outgoingBookingDto);

        List<OutgoingBookingDto> results = bookingService.getBookingsByOwnerAndState(user.getId(), "PAST", 0, 1);

        assertEquals(1, results.size());
        assertEquals(outgoingBookingDto.getId(), results.get(0).getId());
    }

    @Test
    void testGetBookingsByOwnerAndState_future() {
        List<Booking> bookings = Collections.singletonList(booking);
        when(userService.findById(user.getId())).thenReturn(userDto);
        when(bookingRepository.findAllByItemOwnerIdAndStartTimeAfterOrderByStartTimeDesc(any(), any(), any())).thenReturn(bookings);
        when(bookingDtoMapper.toDto(booking)).thenReturn(outgoingBookingDto);

        List<OutgoingBookingDto> results = bookingService.getBookingsByOwnerAndState(user.getId(), "FUTURE", 0, 1);

        assertEquals(1, results.size());
        assertEquals(outgoingBookingDto.getId(), results.get(0).getId());
    }

    @Test
    void testGetBookingsByOwnerAndState_waiting() {
        List<Booking> bookings = Collections.singletonList(booking);
        when(userService.findById(user.getId())).thenReturn(userDto);
        when(bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartTimeDesc(any(), any(), any())).thenReturn(bookings);
        when(bookingDtoMapper.toDto(booking)).thenReturn(outgoingBookingDto);

        List<OutgoingBookingDto> results = bookingService.getBookingsByOwnerAndState(user.getId(), "WAITING", 0, 1);

        assertEquals(1, results.size());
        assertEquals(outgoingBookingDto.getId(), results.get(0).getId());
    }

    @Test
    void testGetBookingsByOwnerAndState_rejected() {
        List<Booking> bookings = Collections.singletonList(booking);
        when(userService.findById(user.getId())).thenReturn(userDto);
        when(bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartTimeDesc(any(), any(), any())).thenReturn(bookings);
        when(bookingDtoMapper.toDto(booking)).thenReturn(outgoingBookingDto);

        List<OutgoingBookingDto> results = bookingService.getBookingsByOwnerAndState(user.getId(), "REJECTED", 0, 1);

        assertEquals(1, results.size());
        assertEquals(outgoingBookingDto.getId(), results.get(0).getId());
    }

    @Test
    void testGetBookingsByOwnerAndState_unknownState() {
        assertThrows(ValidationException.class, () -> bookingService.getBookingsByOwnerAndState(user.getId(), "UNKNOWN", 0, 1));
    }

}

