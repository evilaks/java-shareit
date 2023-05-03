package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.web.servlet.MockMvc;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@SqlGroup({
        @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:test-data.sql"),
        @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:data.sql")
})
public class ItemRequestControllerIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testFindAllItemRequests() throws Exception {

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .param("from", "0")
                        .param("size", "100"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].description").value("Request from user1"))
                .andExpect(jsonPath("$[0].created").isNotEmpty());
    }

    @Test
    public void testAddItemRequest() throws Exception {

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 2L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"description\": \"item request description\"}"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(2L))
                .andExpect(jsonPath("$.description").value("item request description"));
    }

    @Test
    public void testAddItemRequestWithWrongItemId() throws Exception {
        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"desc\":100}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("Item request is not valid"));
    }

    @Test
    public void testAddItemRequestWithWrongUserId() throws Exception {
        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 99L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"description\": \"item request description\"}"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("User with id=99 not found"));
    }
}

