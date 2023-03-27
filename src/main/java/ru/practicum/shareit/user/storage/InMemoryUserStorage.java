package ru.practicum.shareit.user.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Slf4j
@Repository
@RequiredArgsConstructor
public class InMemoryUserStorage implements UserStorage {

    private final HashMap<Long, User> users = new HashMap<>();
    private Long id = 0L;

    @Override
    public List<User> findAll() {
        log.debug("UserStorage: findAll method called");
        return new ArrayList<>(users.values());
    }

    @Override
    public User findById(long userId) {
        log.debug("UserStorage: findById method called with userId={}", userId);
        return users.getOrDefault(userId, null);
    }

    @Override
    public User add(User user) {
        log.debug("UserStorage: add method called with User={}", user);
        Long id = this.generateId();
        user.setId(id);
        users.put(id, user);
        return user;
    }

    @Override
    public User update(long userId, User user) {
        log.debug("UserStorage: update method called with userId={} and User={}", userId, user);
        user.setId(userId);
        users.put(userId, user);
        return user;
    }

    @Override
    public void delete(long userId) {
        log.debug("UserStorage: delete method called with userId={}", userId);
        users.remove(userId);
    }

    private Long generateId() {
        return ++id;
    }
}
