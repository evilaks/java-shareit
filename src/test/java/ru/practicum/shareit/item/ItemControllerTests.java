package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.service.ItemService;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(ItemController.class)
public class ItemControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemService itemService;

    private ItemDto itemDto;
    private ItemWithBookingsDto itemWithBookingsDto;

    @BeforeEach
    public void setUp() {
        itemDto = ItemDto.builder()
                .id(1L)
                .name("Item 1")
                .description("Item 1 description")
                .build();
        itemWithBookingsDto = ItemWithBookingsDto.builder()
                .id(1L)
                .name("Item 1")
                .description("Item 1 description")
                .available(true)
                .lastBooking(null)
                .nextBooking(null)
                .build();
    }

    @Test
    public void testAddItem() throws Exception {
        when(itemService.add(anyLong(), any(ItemDto.class))).thenReturn(itemDto);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Item 1\",\"description\":\"Item 1 description\"}"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Item 1"))
                .andExpect(jsonPath("$.description").value("Item 1 description"));

        verify(itemService, times(1)).add(anyLong(), any(ItemDto.class));
    }

    @Test
    public void testUpdateItem() throws Exception {
        when(itemService.update(anyLong(), anyLong(), any(ItemDto.class))).thenReturn(itemDto);

        mockMvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Item 1\",\"description\":\"Item 1 description\"}"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Item 1"))
                .andExpect(jsonPath("$.description").value("Item 1 description"));

        verify(itemService, times(1)).update(anyLong(), anyLong(), any(ItemDto.class));
    }

    @Test
    public void testFindAllItems() throws Exception {
        List<ItemWithBookingsDto> items = Collections.singletonList(itemWithBookingsDto);
        when(itemService.findAll(anyLong(), anyInt(), anyInt())).thenReturn(items);

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .param("from", "0")
                        .param("size", "100"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Item 1"))
                .andExpect(jsonPath("$[0].description").value("Item 1 description"));

        verify(itemService, times(1)).findAll(anyLong(), anyInt(), anyInt());
    }

    @Test
    public void testFindItemById() throws Exception {
        when(itemService.findById(anyLong(), anyLong())).thenReturn(itemWithBookingsDto);

        mockMvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Item 1"))
                .andExpect(jsonPath("$.description").value("Item 1 description"));

        verify(itemService, times(1)).findById(anyLong(), anyLong());
    }

    @Test
    public void testSearchItems() throws Exception {
        List<ItemDto> items = Collections.singletonList(itemDto);
        when(itemService.search(anyLong(), anyString(), anyInt(), anyInt())).thenReturn(items);

        mockMvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", 1L)
                        .param("text", "Item")
                        .param("from", "0")
                        .param("size", "100"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Item 1"))
                .andExpect(jsonPath("$[0].description").value("Item 1 description"));

        verify(itemService, times(1)).search(anyLong(), anyString(), anyInt(), anyInt());
    }

    @Test
    public void testDeleteItemById() throws Exception {
        doNothing().when(itemService).delete(anyLong(), anyLong());

        mockMvc.perform(delete("/items/1")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk());

        verify(itemService, times(1)).delete(anyLong(), anyLong());
    }

    @Test
    public void testAddComment() throws Exception {
        CommentDto commentDto = new CommentDto(1L, "Test comment", "User 1", "2023 01 01 00:00:00");
        when(itemService.addComment(anyLong(), anyLong(), any(Comment.class))).thenReturn(commentDto);

        mockMvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"text\":\"Test comment\"}"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.text").value("Test comment"))
                .andExpect(jsonPath("$.authorName").value("User 1"));

        verify(itemService, times(1)).addComment(anyLong(), anyLong(), any(Comment.class));
    }

}

