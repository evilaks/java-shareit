package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.InMemoryUserStorage;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserStorageTests {

    private UserStorage userStorage;
    private User testUser1;
    private User testUser2;

    @BeforeEach
    public void beforeEach() {
        userStorage = new InMemoryUserStorage();
        testInit();
    }

    @Test
    public void addTest() {
        User actual = userStorage.add(testUser1);

        assertThat(actual).isNotNull();
        assertThat(actual).hasFieldOrPropertyWithValue("id", 1L);
    }

    @Test
    public void findTest() {
        userStorage.add(testUser1);

        User actual = userStorage.findById(1L);

        assertEquals(testUser1.getName(), actual.getName());
        assertEquals(testUser1.getEmail(), actual.getEmail());
    }

    @Test
    public void findAllTest() {
        userStorage.add(testUser1);
        userStorage.add(testUser2);

        List<User> actual = userStorage.findAll();

        assertThat(actual).isNotEmpty();
        assertThat(actual).size().isEqualTo(2);

    }

    @Test
    public void updateTest() {
        userStorage.add(testUser1);

        testUser1.setName("updated_name");
        testUser1.setEmail("updatedemail@example.com");

        userStorage.update(1L, testUser1);

        User actual = userStorage.findById(1L);

        assertEquals(actual, testUser1);
    }

    @Test
    public void deleteTest() {
        userStorage.add(testUser1);

        userStorage.delete(1L);

        assertThat(userStorage.findById(1L)).isNull();
    }

    private void testInit() {
        testUser1 = User.builder()
                .name("username")
                .email("user@example.com")
                .build();

        testUser2 = User.builder()
                .name("second_username")
                .email("seconduser@example.com")
                .build();
    }
}
