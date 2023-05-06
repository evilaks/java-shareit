package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoMapperImpl;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ItemDtoMapperImplTest {

    private ItemDtoMapperImpl itemDtoMapper;

    @BeforeEach
    public void setUp() {
        itemDtoMapper = new ItemDtoMapperImpl();
    }

    @Test
    public void testToItemDto() {
        User owner = new User();
        owner.setId(1L);
        owner.setName("User 1");

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(1L);

        Item item = new Item();
        item.setId(1L);
        item.setOwner(owner);
        item.setItemRequest(itemRequest);
        item.setName("Test item");
        item.setDescription("Test description");
        item.setIsAvailable(true);

        ItemDto itemDto = itemDtoMapper.toItemDto(item);

        assertThat(itemDto).isNotNull();
        assertThat(itemDto.getId()).isEqualTo(1L);
        assertThat(itemDto.getName()).isEqualTo("Test item");
        assertThat(itemDto.getDescription()).isEqualTo("Test description");
        assertThat(itemDto.getAvailable()).isTrue();
        assertThat(itemDto.getRequestId()).isEqualTo(1L);
    }

    @Test
    public void testToItem() {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("Test item");
        itemDto.setDescription("Test description");
        itemDto.setAvailable(true);
        itemDto.setRequestId(1L);

        User owner = new User();
        owner.setId(1L);
        owner.setName("User 1");

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(1L);

        Item item = itemDtoMapper.toItem(itemDto, owner, itemRequest);


        assertThat(item).isNotNull();
        assertThat(item.getId()).isEqualTo(1L);
        assertThat(item.getName()).isEqualTo("Test item");
        assertThat(item.getDescription()).isEqualTo("Test description");
        assertThat(item.getIsAvailable()).isTrue();
        assertThat(item.getOwner()).isEqualTo(owner);
        assertThat(item.getItemRequest()).isEqualTo(itemRequest);
    }

    @Test
    public void testToItemWithBookingsDto() {

        Item item = new Item();
        item.setId(1L);
        item.setName("Test item");
        item.setDescription("Test description");
        item.setIsAvailable(true);

        User owner = new User();
        owner.setId(1L);
        owner.setName("User 1");
        item.setOwner(owner);

        ItemBookingDto lastBooking = new ItemBookingDto();
        lastBooking.setId(1L);
        lastBooking.setBookerId(2L);
        ItemBookingDto nextBooking = new ItemBookingDto();
        nextBooking.setId(2L);
        nextBooking.setBookerId(3L);

        CommentDto comment = new CommentDto();
        comment.setId(1L);
        comment.setAuthorName("User 1");
        comment.setText("Comment 1");

        List<CommentDto> comments = List.of(comment);

        ItemWithBookingsDto itemWithBookingsDto = itemDtoMapper.toItemWithBookingsDto(item, lastBooking, nextBooking, comments);

        assertThat(itemWithBookingsDto).isNotNull();
        assertThat(itemWithBookingsDto.getId()).isEqualTo(1L);
        assertThat(itemWithBookingsDto.getName()).isEqualTo("Test item");
        assertThat(itemWithBookingsDto.getDescription()).isEqualTo("Test description");
        assertThat(itemWithBookingsDto.getAvailable()).isTrue();
        assertThat(itemWithBookingsDto.getNextBooking()).isNotNull();
        assertThat(itemWithBookingsDto.getLastBooking()).isNotNull();
        assertThat(itemWithBookingsDto.getComments()).isNotNull();
    }
}
