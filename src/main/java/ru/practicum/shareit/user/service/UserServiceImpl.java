package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;
import ru.practicum.shareit.util.exception.NotFoundException;
import ru.practicum.shareit.util.exception.ValidationException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserStorage userStorage;

    @Override
    public User findById(long userId) {
        if (this.doesExist(userId)) {
            return userStorage.findById(userId);
        } else throw new NotFoundException("User with id=" + userId + " not found");
    }

    @Override
    public List<User> findAll() {
        return userStorage.findAll();
    }

    @Override
    public User add(User user) {
        if (this.isValidUser(user)) {
            return userStorage.add(user);
        } else throw new ValidationException("Invalid user object received");
    }

    @Override
    public User update(User user) {
        if (this.isValidUser(user)) {
            if (this.doesExist(user.getId())) {
                return userStorage.update(user);
            } else throw new NotFoundException("User with id=" + user.getId() + " not found");
        } else throw new ValidationException("Invalid user object received");
    }

    @Override
    public void delete(long userId) {
        if (this.doesExist(userId)) {
            userStorage.delete(userId);
        } else throw new NotFoundException("User with id=" + userId + " not found");
    }

    private boolean isValidUser(User user) {
        if (user.getName().isBlank()) {
            return false;
        }

        if (user.getEmail().isBlank()) {
            return false;
        }

        return true;
    }

    private boolean doesExist(Long userId) {
        return userStorage.findById(userId) != null;
    }
}
