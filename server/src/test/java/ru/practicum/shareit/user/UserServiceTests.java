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
    void testFindById_userExists() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userDtoMapper.toUserDto(user)).thenReturn(userDto);

        UserDto foundUserDto = userService.findById(1L);
        assertEquals(userDto, foundUserDto);
    }

    @Test
    void testFindById_userNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> userService.findById(1L));
    }

    @Test
    void testFindAll() {
        List<User> users = Arrays.asList(user, new User(2L, "Jane Doe", "jane.doe@example.com"));
        when(userRepository.findAll()).thenReturn(users);
        when(userDtoMapper.toUserDto(any(User.class))).thenReturn(userDto);

        List<UserDto> userDtos = userService.findAll();
        assertEquals(users.size(), userDtos.size());
    }

    @Test
    void testAdd_validUser() {
        when(userDtoMapper.toUser(userDto)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);
        when(userDtoMapper.toUserDto(user)).thenReturn(userDto);

        UserDto addedUserDto = userService.add(userDto);
        assertEquals(userDto, addedUserDto);
    }

    @Test
    void testUpdate_existingUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);
        when(userDtoMapper.toUserDto(user)).thenReturn(userDto);

        UserDto updatedUserDto = userService.update(1L, userDto);
        assertEquals(userDto, updatedUserDto);
    }

    @Test
    void testUpdate_nonExistingUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> userService.update(1L, userDto));
    }

    @Test
    void testDelete_existingUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        userService.delete(1L);
        verify(userRepository).deleteById(1L);
    }

    @Test
    void testDelete_nonExistingUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> userService.delete(1L));
    }
}
