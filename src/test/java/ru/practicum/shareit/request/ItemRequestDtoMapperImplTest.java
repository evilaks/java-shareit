package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoMapperImpl;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

public class ItemRequestDtoMapperImplTest {

    @InjectMocks
    private ItemRequestDtoMapperImpl itemRequestDtoMapper;

    @Mock
    private ItemDtoMapper itemDtoMapper;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testToItemRequestDto() {
        User author = new User();
        author.setId(1L);
        author.setName("User 1");

        Item item = new Item();
        item.setId(1L);
        item.setName("Item 1");
        item.setDescription("Item description 1");
        item.setIsAvailable(true);

        List<Item> items = List.of(item);

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        itemRequest.setAuthor(author);
        itemRequest.setDescription("Item request description");
        itemRequest.setCreated(LocalDateTime.now());
        itemRequest.setItems(items);

        when(itemDtoMapper.toItemDto(item)).thenReturn(new ItemDto(1L, "Item 1", "Item description 1", true, 1L));

        ItemRequestDto itemRequestDto = itemRequestDtoMapper.toItemRequestDto(itemRequest, items);

        assertThat(itemRequestDto).isNotNull();
        assertThat(itemRequestDto.getId()).isEqualTo(1L);
        assertThat(itemRequestDto.getDescription()).isEqualTo("Item request description");
        assertThat(itemRequestDto.getCreated()).isNotNull();
        assertThat(itemRequestDto.getItems()).isNotNull();
        assertThat(itemRequestDto.getItems().size()).isEqualTo(1);
        assertThat(itemRequestDto.getItems().get(0).getId()).isEqualTo(1L);
        assertThat(itemRequestDto.getItems().get(0).getName()).isEqualTo("Item 1");
        assertThat(itemRequestDto.getItems().get(0).getDescription()).isEqualTo("Item description 1");
        assertThat(itemRequestDto.getItems().get(0).getAvailable()).isTrue();
        assertThat(itemRequestDto.getItems().get(0).getRequestId()).isEqualTo(1L);
    }

    @Test
    public void testToItemRequest() {
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setId(1L);
        itemRequestDto.setDescription("Item request description");
        itemRequestDto.setCreated("2021-01-01T00:00:00.000");

        User author = new User();
        author.setId(1L);
        author.setName("User 1");


        ItemRequest itemRequest = itemRequestDtoMapper.toItemRequest(itemRequestDto, author);

        assertThat(itemRequest).isNotNull();
        assertThat(itemRequest.getId()).isEqualTo(1L);
        assertThat(itemRequest.getDescription()).isEqualTo("Item request description");
        assertThat(itemRequest.getCreated()).isNotNull();
        assertThat(itemRequest.getAuthor()).isNotNull();

        assertThat(itemRequest.getAuthor().getId()).isEqualTo(1L);
        assertThat(itemRequest.getAuthor().getName()).isEqualTo("User 1");
    }
}
