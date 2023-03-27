package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {

    User findById(long userId);

    List<User> findAll();

    User add(User user);

    User update(Long userId, User user);

    void delete(long userId);

}
