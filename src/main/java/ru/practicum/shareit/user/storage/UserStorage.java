package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserStorage {

    List<User> findAll();

    User findById(long userId);

    User add(User user);

    User update(User user);

    void delete(long userId);
}
