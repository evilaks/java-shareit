package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.controller.ItemRequestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(ItemRequestController.class)
public class ItemRequestControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemRequestService itemRequestService;

    private ItemRequestDto itemRequestDto;

    @BeforeEach
    public void setUp() {
        itemRequestDto = new ItemRequestDto();
        itemRequestDto.setId(1L);
        itemRequestDto.setDescription("Item request description");
    }

    @Test
    public void createItemRequest() throws Exception {
        when(itemRequestService.createItemRequest(1L, itemRequestDto)).thenReturn(itemRequestDto);

        mockMvc.perform(post("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "1")
                        .content("{\"id\":1,\"description\":\"Item request description\"}"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.description").value("Item request description"));

        verify(itemRequestService, times(1)).createItemRequest(1L, itemRequestDto);
    }

    @Test
    public void getUserItemRequests() throws Exception {
        List<ItemRequestDto> itemRequestDtoList = Collections.singletonList(itemRequestDto);
        when(itemRequestService.getUserItemRequests(1L)).thenReturn(itemRequestDtoList);

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].description").value("Item request description"));

        verify(itemRequestService, times(1)).getUserItemRequests(1L);
    }

    @Test
    public void getOtherUsersItemRequests() throws Exception {
        List<ItemRequestDto> itemRequestDtoList = Collections.singletonList(itemRequestDto);
        when(itemRequestService.getOtherUsersItemRequests(1L, 0, 100)).thenReturn(itemRequestDtoList);

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].description").value("Item request description"));

        verify(itemRequestService, times(1)).getOtherUsersItemRequests(1L, 0, 100);
    }

    @Test
    public void getItemRequestById() throws Exception {
        when(itemRequestService.getItemRequestById(1L, 1L)).thenReturn(itemRequestDto);

        mockMvc.perform(get("/requests/1")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.description").value("Item request description"));

        verify(itemRequestService, times(1)).getItemRequestById(1L, 1L);
    }
}
