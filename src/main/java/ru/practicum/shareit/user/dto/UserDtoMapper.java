package ru.practicum.shareit.user.dto;

import ru.practicum.shareit.user.model.User;

public class UserDtoMapper {
    public static UserDto userToUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .build();
    }
}