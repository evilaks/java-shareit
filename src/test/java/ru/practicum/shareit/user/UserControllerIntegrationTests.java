package ru.practicum.shareit.user;

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
public class UserControllerIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    // test addUser
    @Test
    public void testAddUser() throws Exception {

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"user name\", \"email\": \"user@email.com\"}"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(8L))
                .andExpect(jsonPath("$.name").value("user name"))
                .andExpect(jsonPath("$.email").value("user@email.com"));
    }

    // test updateUser
    @Test
    public void testUpdateUser() throws Exception {

        mockMvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"new user name\", \"email\": \"new@email.com\"}"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("new user name"))
                .andExpect(jsonPath("$.email").value("new@email.com"));
    }

    // test findAllUsers
    @Test
    public void testFindAllUsers() throws Exception {

        mockMvc.perform(get("/users")
                        .param("from", "0")
                        .param("size", "100"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("updateName"))
                .andExpect(jsonPath("$[0].email").value("updateName@user.com"));
    }

    // test findUserById
    @Test
    public void testFindUserById() throws Exception {

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("updateName"))
                .andExpect(jsonPath("$.email").value("updateName@user.com"));
    }

    // test deleteUserById
    @Test
    public void testDeleteUserById() throws Exception {

        mockMvc.perform(delete("/users/7"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/users/7"))
                .andExpect(status().isNotFound());
    }
}
