package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.util.exception.NotFoundException;
import ru.practicum.shareit.util.exception.ValidationException;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceTests {

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Mock
    private ItemRequestDtoMapper itemRequestDtoMapper;

    @Mock
    private UserService userService;

    @Mock
    private UserDtoMapper userDtoMapper;

    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;

    private User user;
    private ItemRequest itemRequest;
    private ItemRequestDto itemRequestDto;

    @BeforeEach
    void setUp() {
        LocalDateTime time = LocalDateTime.of(2023, 1, 1, 0, 0);
        user = new User(1L, "John Doe", "john.doe@example.com");
        itemRequest = new ItemRequest(1L, "Item Request", time, null, user);
        itemRequestDto = new ItemRequestDto(1L, "Item Request", null, new ArrayList<>());
    }

    @Test
    void createItemRequest_validRequest() {
        when(userService.findById(1L)).thenReturn(new UserDto(1L, "John Doe", "john.doe@example.com"));
        when(itemRequestDtoMapper.toItemRequest(itemRequestDto, user)).thenReturn(itemRequest);
        when(itemRequestRepository.save(itemRequest)).thenReturn(itemRequest);
        when(itemRequestDtoMapper.toItemRequestDto(itemRequest, itemRequest.getItems())).thenReturn(itemRequestDto);
        when(userDtoMapper.toUser(new UserDto(1L, "John Doe", "john.doe@example.com"))).thenReturn(user);

        ItemRequestDto createdItemRequestDto = itemRequestService.createItemRequest(1L, itemRequestDto);
        assertEquals(itemRequestDto, createdItemRequestDto);
    }

    @Test
    void createItemRequest_invalidRequest() {
        ItemRequestDto invalidItemRequestDto = new ItemRequestDto(1L, "", null, new ArrayList<>());
        assertThrows(ValidationException.class, () -> itemRequestService.createItemRequest(1L, invalidItemRequestDto));
    }

    @Test
    void getUserItemRequests() {
        List<ItemRequest> itemRequests = Collections.singletonList(itemRequest);
        when(userService.findById(1L)).thenReturn(new UserDto(1L, "John Doe", "john.doe@example.com"));
        when(itemRequestRepository.findAllByAuthorId(1L)).thenReturn(itemRequests);
        when(itemRequestDtoMapper.toItemRequestDto(any(ItemRequest.class), any())).thenReturn(itemRequestDto);

        List<ItemRequestDto> itemRequestDtos = itemRequestService.getUserItemRequests(1L);
        assertEquals(itemRequests.size(), itemRequestDtos.size());
    }

    @Test
    void getOtherUsersItemRequests() {
        List<ItemRequest> itemRequests = Collections.singletonList(itemRequest);
        when(userService.findById(1L)).thenReturn(new UserDto(1L, "John Doe", "john.doe@example.com"));
        when(itemRequestRepository.findAllByAuthorIdNot(eq(1L), any())).thenReturn(itemRequests);
        when(itemRequestDtoMapper.toItemRequestDto(any(ItemRequest.class), any())).thenReturn(itemRequestDto);

        List<ItemRequestDto> itemRequestDtos = itemRequestService.getOtherUsersItemRequests(1L, 0, 10);
        assertEquals(itemRequests.size(), itemRequestDtos.size());
    }

    @Test
    void getItemRequestById_found() {
        when(userService.findById(1L)).thenReturn(new UserDto(1L, "John Doe", "john.doe@example.com"));
        when(itemRequestRepository.findById(1L)).thenReturn(Optional.of(itemRequest));
        when(itemRequestDtoMapper.toItemRequestDto(itemRequest, itemRequest.getItems())).thenReturn(itemRequestDto);

        ItemRequestDto foundItemRequestDto = itemRequestService.getItemRequestById(1L, 1L);
        assertEquals(itemRequestDto, foundItemRequestDto);
    }

    @Test
    void getItemRequestById_notFound() {
        when(userService.findById(1L)).thenReturn(new UserDto(1L, "John Doe", "john.doe@example.com"));
        when(itemRequestRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemRequestService.getItemRequestById(1L, 1L));
    }
}


