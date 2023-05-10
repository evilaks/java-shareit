package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.*;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDtoMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDtoMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookingDtoMapperTest {

    @Mock
    private UserDtoMapper userDtoMapper;

    @Mock
    private ItemDtoMapper itemDtoMapper;

    @InjectMocks
    private BookingDtoMapperImpl bookingDtoMapper;

    private Booking booking;
    private IncomingBookingDto incomingBookingDto;
    private User booker;
    private UserDto bookerDto;
    private Item item;
    private ItemDto itemDto;

    @BeforeEach
    public void setUp() {
        booker = new User(1L, "User 1", "booker@example.com");
        bookerDto = new UserDto(1L, "User 1", "booker@example.com");

        User owner = new User(2L, "User 2", "owner@example.com");

        item = new Item(1L, "Item 1", "Item 1 description", true, owner, null);
        itemDto = new ItemDto(1L, "Item 1", "Item 1 description", true, null);

        booking = new Booking(1L, LocalDateTime.parse("2023-01-01T00:00"), LocalDateTime.parse("2023-01-10T00:00"), item, booker, BookingStatus.WAITING);

        incomingBookingDto = new IncomingBookingDto(1L,
                LocalDateTime.parse("2023-01-01T00:00"),
                LocalDateTime.parse("2023-01-10T00:00"),
                1L,
                1L,
                BookingStatus.WAITING);
    }

    @Test
    public void testToDto() {
        when(userDtoMapper.toUserDto(booker)).thenReturn(bookerDto);
        when(itemDtoMapper.toItemDto(item)).thenReturn(itemDto);

        OutgoingBookingDto outgoingBookingDto = bookingDtoMapper.toDto(booking);

        assertThat(outgoingBookingDto).isNotNull();
        assertThat(outgoingBookingDto.getId()).isEqualTo(1L);
        assertThat(outgoingBookingDto.getStart()).isEqualTo("2023-01-01T00:00");
        assertThat(outgoingBookingDto.getEnd()).isEqualTo("2023-01-10T00:00");
        assertThat(outgoingBookingDto.getStatus()).isEqualTo(BookingStatus.WAITING);
        assertThat(outgoingBookingDto.getItem().getId()).isEqualTo(1L);
        assertThat(outgoingBookingDto.getBooker().getId()).isEqualTo(1L);
    }

    @Test
    public void testToBooking() {
        Booking result = bookingDtoMapper.toBooking(incomingBookingDto, item, booker);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getStartTime()).isEqualTo(LocalDateTime.parse("2023-01-01T00:00"));
        assertThat(result.getEndTime()).isEqualTo(LocalDateTime.parse("2023-01-10T00:00"));
        assertThat(result.getStatus()).isEqualTo(BookingStatus.WAITING);
        assertThat(result.getItem()).isEqualTo(item);
        assertThat(result.getBooker()).isEqualTo(booker);
    }

    @Test
    public void testToItemBookingDto() {
        ItemBookingDto itemBookingDto = bookingDtoMapper.toItemBookingDto(booking);
        assertThat(itemBookingDto).isNotNull();
        assertThat(itemBookingDto.getId()).isEqualTo(1L);
        assertThat(itemBookingDto.getBookerId()).isEqualTo(1L);
    }
}
