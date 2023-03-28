package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@Slf4j
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping()
    public UserDto addUser(@RequestBody @Valid UserDto userDto) {
        log.debug("Received POST request to /users endpoint with User-object {}", userDto);
        return userService.add(userDto);
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@PathVariable Long userId, @RequestBody UserDto userDto) {
        log.debug("Received PATCH request to /users/{} endpoint with User-object {}", userId, userDto);
        return userService.update(userId, userDto);
    }

    @GetMapping()
    public List<UserDto> findAllUsers() {
        log.debug("Received GET request to /users endpoint");
        return userService.findAll();
    }

    @GetMapping("/{userId}")
    public UserDto findUserById(@PathVariable Long userId) {
        log.debug("Received GET request to /users/{} endpoint", userId);
        return userService.findById(userId);
    }

    @DeleteMapping("/{userId}")
    public void deleteUserById(@PathVariable Long userId) {
        log.debug("Received DELETE request to /users/{} endpoint", userId);
        userService.delete(userId);
    }
}

/*  todo

    Создайте класс UserController и методы в нём для основных CRUD-операций.
    Также реализуйте сохранение данных о пользователях в памяти.

    Сделать интерфейс сервиса и его имплементацию.

    Сделать хранение в памяти, тоже через интерфейс и его имплементацию.

    Эндпоинты:

    POST /users - create user
    PATCH /users/id - update user
    GET /users - get all users
    GET /users/id - get user by id
    DELETE /users/id - delete user by id

 */
