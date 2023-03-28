package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {

    UserDto findById(long userId);

    List<UserDto> findAll();

    UserDto add(UserDto userDto);

    UserDto update(Long userId, UserDto userDto);

    void delete(long userId);

}
