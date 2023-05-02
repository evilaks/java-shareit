package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserServiceImpl;
import ru.practicum.shareit.user.storage.UserRepository;
import ru.practicum.shareit.util.exception.BadRequestException;
import ru.practicum.shareit.util.exception.NotFoundException;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTests {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserDtoMapper userDtoMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .name("John Doe")
                .email("john.doe@example.com")
                .build();
        userDto = UserDto.builder()
                .id(1L)
                .name("John Doe")
                .email("john.doe@example.com")
                .build();
    }

    @Test
    void findById_userExists() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userDtoMapper.toUserDto(user)).thenReturn(userDto);

        UserDto foundUserDto = userService.findById(1L);
        assertEquals(userDto, foundUserDto);
    }

    @Test
    void findById_userNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> userService.findById(1L));
    }

    @Test
    void findAll() {
        List<User> users = Arrays.asList(user, new User(2L, "Jane Doe", "jane.doe@example.com"));
        when(userRepository.findAll()).thenReturn(users);
        when(userDtoMapper.toUserDto(any(User.class))).thenReturn(userDto);

        List<UserDto> userDtos = userService.findAll();
        assertEquals(users.size(), userDtos.size());
    }

    @Test
    void add_validUser() {
        when(userDtoMapper.toUser(userDto)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);
        when(userDtoMapper.toUserDto(user)).thenReturn(userDto);

        UserDto addedUserDto = userService.add(userDto);
        assertEquals(userDto, addedUserDto);
    }

    @Test
    void add_invalidUser() {
        UserDto invalidUserDto = new UserDto(1L, "", "invalid.email");
        assertThrows(BadRequestException.class, () -> userService.add(invalidUserDto));
    }

    @Test
    void update_existingUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);
        when(userDtoMapper.toUserDto(user)).thenReturn(userDto);

        UserDto updatedUserDto = userService.update(1L, userDto);
        assertEquals(userDto, updatedUserDto);
    }

    @Test
    void update_nonExistingUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> userService.update(1L, userDto));
    }

    @Test
    void delete_existingUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        userService.delete(1L);
        verify(userRepository).deleteById(1L);
    }

    @Test
    void delete_nonExistingUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> userService.delete(1L));
    }
}
