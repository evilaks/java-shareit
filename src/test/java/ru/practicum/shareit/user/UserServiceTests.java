package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.service.UserServiceImpl;
import ru.practicum.shareit.user.storage.UserStorage;
import ru.practicum.shareit.util.exception.NotFoundException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserServiceTests {

    private final UserStorage userStorage = Mockito.mock(UserStorage.class);
    private UserService userService;
    private User testUser1;
    private User testUser2;


    @BeforeEach
    public void beforeEach() {
        userService = new UserServiceImpl(userStorage);
        testInit();
    }

    @Test
    public void findByIdTest() {

        Mockito.when(userStorage.findById(1L)).thenReturn(testUser1);

        User actual = userService.findById(1L);

        assertThat(actual).isNotNull();
    }

    @Test
    public void findAllTest() {
        Mockito.when(userStorage.findAll()).thenReturn(List.of(testUser1, testUser2));

        List<User> actual = userService.findAll();

        assertThat(actual).size().isEqualTo(2);
    }

    @Test
    public void addTest() {
        Mockito.when(userStorage.add(testUser1)).thenReturn(testUser1);

        User actual = userService.add(testUser1);

        assertThat(actual).isNotNull();
    }

    @Test
    public void updateTest() {
        testUser1.setId(1L);
        Mockito.when(userStorage.findById(1L)).thenReturn(testUser1);
        Mockito.when(userStorage.update(1L, testUser1)).thenReturn(testUser1);

        testUser1.setName("updatedName");
        testUser1.setEmail("updatedemail@example.com");
        User actual = userService.update(1L, testUser1);

        assertThat(actual).isEqualTo(testUser1);
    }

    @Test
    public void deleteTest() {
        Mockito.when(userStorage.findById(1L)).thenReturn(testUser1, null);  // todo change to exception
        Mockito.doNothing().when(userStorage).delete(1L);

        userService.delete(1L);

        assertThrows(NotFoundException.class, () -> userService.findById(1L));
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
