package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoMapper;
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
    private final UserDtoMapper userDtoMapper = Mockito.mock(UserDtoMapper.class);
    private UserService userService;
    private User testUser1;
    private User testUser2;
    private UserDto testUserDto1;


    @BeforeEach
    public void beforeEach() {
        userService = new UserServiceImpl(userStorage, userDtoMapper);
        testInit();
    }

    @Test
    public void findByIdTest() {

        Mockito.when(userStorage.findById(1L)).thenReturn(testUser1);
        Mockito.when(userDtoMapper.toUserDto(testUser1)).thenReturn(testUserDto1);

        UserDto actual = userService.findById(1L);

        assertThat(actual).isNotNull();
    }

    @Test
    public void findAllTest() {
        Mockito.when(userStorage.findAll()).thenReturn(List.of(testUser1, testUser2));

        List<UserDto> actual = userService.findAll();

        assertThat(actual).size().isEqualTo(2);
    }

    @Test
    public void addTest() {
        Mockito.when(userStorage.add(testUser1)).thenReturn(testUser1);
        Mockito.when(userDtoMapper.toUser(testUserDto1)).thenReturn(testUser1);
        Mockito.when(userDtoMapper.toUserDto(testUser1)).thenReturn(testUserDto1);

        UserDto actual = userService.add(testUserDto1);

        assertThat(actual).isNotNull();
    }

    @Test
    public void updateTest() {
        testUser1.setId(1L);
        Mockito.when(userStorage.findById(1L)).thenReturn(testUser1);
        Mockito.when(userStorage.update(1L, testUser1)).thenReturn(testUser1);
        Mockito.when(userDtoMapper.toUser(testUserDto1)).thenReturn(testUser1);
        Mockito.when(userDtoMapper.toUserDto(testUser1)).thenReturn(UserDto.builder()
                        .name("updatedName")
                        .email("updatedemail@example.com")
                        .build());

        testUserDto1.setName("updatedName");
        testUserDto1.setEmail("updatedemail@example.com");
        UserDto actual = userService.update(1L, testUserDto1);

        assertThat(actual).hasFieldOrPropertyWithValue("name", testUser1.getName())
                .hasFieldOrPropertyWithValue("email", testUser1.getEmail());

    }

    @Test
    public void deleteTest() {
        Mockito.when(userStorage.findById(1L)).thenReturn(testUser1, null);
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

        testUserDto1 = UserDto.builder()
                .name("username")
                .email("user@example.com")
                .build();

    }

}
