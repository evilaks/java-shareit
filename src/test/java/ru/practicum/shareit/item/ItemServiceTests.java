package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.dto.BookingDtoMapper;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.item.storage.CommentRepository;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.util.exception.BadRequestException;
import ru.practicum.shareit.util.exception.NotAllowedException;
import ru.practicum.shareit.util.exception.NotFoundException;
import ru.practicum.shareit.util.exception.ValidationException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceTests {

    @Mock
    private ItemStorage itemStorage;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserService userService;

    @Mock
    private ItemDtoMapper itemDtoMapper;

    @Mock
    private UserDtoMapper userDtoMapper;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private BookingDtoMapper bookingDtoMapper;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private CommentDtoMapper commentDtoMapper;

    @InjectMocks
    private ItemServiceImpl itemService;

    private final Long itemId = 1L;
    private final Long userId = 1L;
    private Item item;
    private User user;

    private UserDto userDto;
    private ItemDto itemDto;

    @BeforeEach
    void setUp() {
        item = new Item();
        item.setId(itemId);
        item.setName("Item");
        item.setDescription("Description");
        item.setIsAvailable(true);

        user = new User();
        user.setId(userId);
        user.setName("Name");
        user.setEmail("email@example.com");

        userDto = UserDto.builder()
                .id(userId)
                .name("Name")
                .email("email@example.com")
                .build();

        itemDto = ItemDto.builder()
                .id(itemId)
                .name("Item")
                .description("Description")
                .available(true)
                .build();

        item.setOwner(user);

    }

    @Test
    void findById_itemExists() {
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(userService.findById(userId)).thenReturn(userDto);

        when(bookingRepository.findFirstByItemIdAndStartTimeBeforeAndStatusOrderByStartTimeDesc(
                eq(itemId), any(LocalDateTime.class), eq(BookingStatus.APPROVED)))
                .thenReturn(Optional.empty());

        when(bookingRepository.findFirstByItemIdAndStartTimeAfterAndStatusOrderByStartTimeAsc(
                eq(itemId), any(LocalDateTime.class), eq(BookingStatus.APPROVED)))
                .thenReturn(Optional.empty());

        when(commentRepository.findAllByItemIdOrderByCreatedDesc(itemId)).thenReturn(new ArrayList<>());

        ItemWithBookingsDto expectedItemDto = ItemWithBookingsDto.builder()
                .id(itemId)
                .name("Item")
                .description("Description")
                .available(true)
                .build();
        when(itemDtoMapper.toItemWithBookingsDto(item, null, null, new ArrayList<>())).thenReturn(expectedItemDto);

        ItemWithBookingsDto actualItemDto = itemService.findById(itemId, userId);
        assertEquals(expectedItemDto, actualItemDto);
    }

    @Test
    void findById_itemDoesNotExist() {
        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());
        when(userService.findById(userId)).thenReturn(userDto);

        assertThrows(NotFoundException.class, () -> itemService.findById(itemId, userId));
    }

    @Test
    void findAll_validParameters() {
        when(userService.findById(userId)).thenReturn(userDto);

        ItemWithBookingsDto expectedItemDto = ItemWithBookingsDto.builder().build();
        when(itemDtoMapper.toItemWithBookingsDto(item, null, null, new ArrayList<>())).thenReturn(expectedItemDto);

        List<Item> itemList = Collections.singletonList(item);
        when(itemRepository.findAllByOwnerIdOrderById(userId, PageRequest.of(0, 10))).thenReturn(itemList);

        List<ItemWithBookingsDto> actualItems = itemService.findAll(userId, 0, 10);
        assertEquals(1, actualItems.size());
        assertEquals(expectedItemDto, actualItems.get(0));
    }

    @Test
    void findAll_invalidParameters() {
        when(userService.findById(userId)).thenReturn(userDto);

        assertThrows(ValidationException.class, () -> itemService.findAll(userId, -1, 10));
        assertThrows(ValidationException.class, () -> itemService.findAll(userId, 0, 0));
    }

    @Test
    void add_validItem() {
        when(itemDtoMapper.toItem(itemDto, user, null)).thenReturn(item);
        when(userService.findById(userId)).thenReturn(userDto);
        when(itemRepository.save(item)).thenReturn(item);
        when(userDtoMapper.toUser(userDto)).thenReturn(user);

        itemDto = itemService.add(userId, itemDto);
        assertEquals(itemId, itemDto.getId());
    }

    @Test
    void add_invalidItem() {
        ItemDto itemDto = ItemDto.builder().build();

        assertThrows(ValidationException.class, () -> itemService.add(userId, itemDto));
    }

    @Test
    void update_validItem() {
        when(userService.findById(userId)).thenReturn(userDto);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        ItemDto itemDto = ItemDto.builder().build();
        itemDto.setName("Updated Name");
        itemDto.setDescription("Updated Description");
        itemDto.setAvailable(true);

        when(itemStorage.update(itemId, item)).thenReturn(item);
        when(itemDtoMapper.toItemDto(item)).thenReturn(itemDto);

        ItemDto updatedItemDto = itemService.update(userId, itemId, itemDto);

        assertEquals("Updated Name", updatedItemDto.getName());
        assertEquals("Updated Description", updatedItemDto.getDescription());
        assertTrue(updatedItemDto.getAvailable());
    }

    @Test
    void update_itemNotFound() {
        when(userService.findById(userId)).thenReturn(userDto);
        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        ItemDto itemDto = ItemDto.builder().build();

        assertThrows(NotFoundException.class, () -> itemService.update(userId, itemId, itemDto));
    }

    @Test
    void delete_validItem() {
        when(userService.findById(userId)).thenReturn(userDto);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        itemService.delete(userId, itemId);

        verify(itemStorage, times(1)).delete(itemId);
    }

    @Test
    void delete_itemNotFound() {
        when(userService.findById(userId)).thenReturn(userDto);
        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.delete(userId, itemId));
    }

    @Test
    void addComment_validComment() {
        when(userService.findById(userId)).thenReturn(userDto);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(bookingRepository.existsByItemIdAndBookerIdAndStatusAndEndTimeBefore(eq(itemId),
                eq(userId), eq(BookingStatus.APPROVED), any(LocalDateTime.class))).thenReturn(true);

        Comment comment = new Comment();
        comment.setText("This is a comment.");

        when(userDtoMapper.toUser(any(UserDto.class))).thenReturn(user);
        Comment savedComment = new Comment();
        savedComment.setId(1L);
        savedComment.setText("This is a comment.");
        when(commentRepository.save(comment)).thenReturn(savedComment);
        when(commentDtoMapper.toDto(savedComment)).thenReturn(new CommentDto());

        CommentDto commentDto = itemService.addComment(userId, itemId, comment);

        assertNotNull(commentDto);
    }

    @Test
    void addComment_emptyComment() {
        when(userService.findById(userId)).thenReturn(userDto);

        Comment comment = new Comment();
        comment.setText("");

        assertThrows(BadRequestException.class, () -> itemService.addComment(userId, itemId, comment));
    }

    @Test
    void search_validSearch() {
        when(userService.findById(userId)).thenReturn(userDto);
        List<Item> itemList = new ArrayList<>();
        itemList.add(item);
        when(itemRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndIsAvailable(anyString(), anyString(), anyBoolean(), any(PageRequest.class))).thenReturn(itemList);
        when(itemDtoMapper.toItemDto(item)).thenReturn(ItemDto.builder().build());

        List<ItemDto> searchResults = itemService.search(userId, "searchQuery", 0, 10);

        assertEquals(1, searchResults.size());
    }

    @Test
    void delete_validDelete() {
        when(userService.findById(userId)).thenReturn(userDto);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        itemService.delete(userId, itemId);

        verify(itemStorage).delete(itemId);
    }

    @Test
    void delete_notAllowedToDelete() {
        when(userService.findById(userId)).thenReturn(userDto);
        Item itemWithDifferentOwner = new Item();
        User differentOwner = new User();
        differentOwner.setId(2L);
        itemWithDifferentOwner.setOwner(differentOwner);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(itemWithDifferentOwner));

        assertThrows(NotAllowedException.class, () -> itemService.delete(userId, itemId));
    }

    @Test
    void delete_notFound() {
        when(userService.findById(userId)).thenReturn(userDto);
        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.delete(userId, itemId));
    }
}
