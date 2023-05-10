package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.controller.BookingController;
import ru.practicum.shareit.booking.dto.IncomingBookingDto;
import ru.practicum.shareit.booking.dto.OutgoingBookingDto;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookingController.class)
public class BookingControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingService bookingService;

    private OutgoingBookingDto outgoingBookingDto;
    private ItemDto itemDto;
    private UserDto userDto;

    @BeforeEach
    public void setUp() {
        userDto = new UserDto(1L, "User 1", "mail@example.com");
        itemDto = new ItemDto(1L, "Item 1", "Item 1 description", true, null);
        outgoingBookingDto = OutgoingBookingDto.builder()
                .id(1L)
                .start(LocalDateTime.of(2023, 1, 1, 0, 0, 0))
                .end(LocalDateTime.of(2023, 1, 1, 0, 1, 0))
                .item(itemDto)
                .booker(userDto)
                .status(BookingStatus.WAITING)
                .build();
    }

    @Test
    public void testCreateBooking() throws Exception {
        when(bookingService.createBooking(anyLong(), any(IncomingBookingDto.class))).thenReturn(outgoingBookingDto);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"itemId\":1,\"fromDate\":\"2023-01-01\",\"toDate\":\"2023-01-10\"}"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.start").value("2023-01-01T00:00:00"))
                .andExpect(jsonPath("$.end").value("2023-01-01T00:01:00"))
                .andExpect(jsonPath("$.item.id").value(1L))
                .andExpect(jsonPath("$.booker.id").value(1L))
                .andExpect(jsonPath("$.status").value("WAITING"));

        verify(bookingService, times(1)).createBooking(anyLong(), any(IncomingBookingDto.class));
    }

    @Test
    public void testUpdateBookingApproval() throws Exception {
        OutgoingBookingDto updatedBookingDto = OutgoingBookingDto.builder()
                .id(1L)
                .start(LocalDateTime.of(2023, 1, 1, 0, 0, 0))
                .end(LocalDateTime.of(2023, 1, 1, 0, 1, 0))
                .item(itemDto)
                .booker(userDto)
                .status(BookingStatus.APPROVED)
                .build();
        when(bookingService.updateBookingApproval(anyLong(), anyLong(), anyBoolean())).thenReturn(updatedBookingDto);

        mockMvc.perform(patch("/bookings/1")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.start").value("2023-01-01T00:00:00"))
                .andExpect(jsonPath("$.end").value("2023-01-01T00:01:00"))
                .andExpect(jsonPath("$.item.id").value(1L))
                .andExpect(jsonPath("$.booker.id").value(1L))
                .andExpect(jsonPath("$.status").value("APPROVED"));

        verify(bookingService, times(1)).updateBookingApproval(anyLong(), anyLong(), anyBoolean());
    }

    @Test
    public void testGetBookingById() throws Exception {
        when(bookingService.getBookingById(anyLong(), anyLong())).thenReturn(outgoingBookingDto);

        mockMvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.start").value("2023-01-01T00:00:00"))
                .andExpect(jsonPath("$.end").value("2023-01-01T00:01:00"))
                .andExpect(jsonPath("$.item.id").value(1L))
                .andExpect(jsonPath("$.booker.id").value(1L))
                .andExpect(jsonPath("$.status").value("WAITING"));

        verify(bookingService, times(1)).getBookingById(anyLong(), anyLong());
    }

    @Test
    public void testGetBookingsByState() throws Exception {
        List<OutgoingBookingDto> bookings = List.of(outgoingBookingDto);
        when(bookingService.getBookingsByState(anyLong(), anyString(), anyInt(), anyInt())).thenReturn(bookings);

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "WAITING")
                        .param("from", "0")
                        .param("size", "100"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].start").value("2023-01-01T00:00:00"))
                .andExpect(jsonPath("$[0].end").value("2023-01-01T00:01:00"))
                .andExpect(jsonPath("$[0].item.id").value(1L))
                .andExpect(jsonPath("$[0].booker.id").value(1L))
                .andExpect(jsonPath("$[0].status").value("WAITING"));

        verify(bookingService, times(1)).getBookingsByState(anyLong(), anyString(), anyInt(), anyInt());
    }

    @Test
    public void testGetBookingsByOwnerAndState() throws Exception {
        List<OutgoingBookingDto> bookings = List.of(outgoingBookingDto);
        when(bookingService.getBookingsByOwnerAndState(anyLong(), anyString(), anyInt(), anyInt())).thenReturn(bookings);
        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "PENDING")
                        .param("from", "0")
                        .param("size", "100"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].start").value("2023-01-01T00:00:00"))
                .andExpect(jsonPath("$[0].end").value("2023-01-01T00:01:00"))
                .andExpect(jsonPath("$[0].item.id").value(1L))
                .andExpect(jsonPath("$[0].booker.id").value(1L))
                .andExpect(jsonPath("$[0].status").value("WAITING"));

        verify(bookingService, times(1)).getBookingsByOwnerAndState(anyLong(), anyString(), anyInt(), anyInt());
    }
}

