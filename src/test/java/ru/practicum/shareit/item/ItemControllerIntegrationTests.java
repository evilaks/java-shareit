package ru.practicum.shareit.item;

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
        @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:clean-data.sql")
})
public class ItemControllerIntegrationTests {

    @Autowired
    private MockMvc mockMvc;


    @Test
    public void testFindAllItems() throws Exception {
        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .param("from", "0")
                        .param("size", "100"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Аккумуляторная дрель"))
                .andExpect(jsonPath("$[0].description").value("Аккумуляторная дрель + аккумулятор"));
    }

    @Test
    public void testAddItem() throws Exception {
        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Item 2\",\"description\":\"Item 2 description\",\"available\":\"true\"}"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("Item 2"))
                .andExpect(jsonPath("$.description").value("Item 2 description"));
    }

    @Test
    public void testUpdateItem() throws Exception {
        mockMvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Updated Item 1\",\"description\":\"Updated Item 1 description\"}"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Updated Item 1"))
                .andExpect(jsonPath("$.description").value("Updated Item 1 description"));
    }

    @Test
    public void testFindItemById() throws Exception {
        mockMvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Аккумуляторная дрель"))
                .andExpect(jsonPath("$.description").value("Аккумуляторная дрель + аккумулятор"));
    }

    @Test
    public void testSearchItems() throws Exception {
        mockMvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", 1L)
                        .param("text", "дрель")
                        .param("from", "0")
                        .param("size", "100"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Аккумуляторная дрель"))
                .andExpect(jsonPath("$[0].description").value("Аккумуляторная дрель + аккумулятор"));
    }

    @Test
    public void testDeleteItemById() throws Exception {
        mockMvc.perform(delete("/items/5")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk());

        // Verify that the item with id=1 is deleted
        mockMvc.perform(get("/items/5")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void testAddComment() throws Exception {

        mockMvc.perform(post("/items/2/comment")
                        .header("X-Sharer-User-Id", 5L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"text\":\"Test comment\"}"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.text").value("Test comment"))
                .andExpect(jsonPath("$.authorName").value("other"));

        // Verify that the comment was added
        mockMvc.perform(get("/items/2")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.comments[0].text").value("Test comment"))
                .andExpect(jsonPath("$.comments[0].authorName").value("other"));
    }
}