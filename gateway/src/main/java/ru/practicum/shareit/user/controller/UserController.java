package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.client.UserClient;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;


@Slf4j
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {

    private final UserClient userClient;

    @PostMapping()
    public ResponseEntity<Object> addUser(@RequestBody @Valid UserDto userDto) {
        log.debug("Received POST request to /users endpoint with User-object {}", userDto);
        return userClient.add(userDto);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateUser(@PathVariable Long userId, @RequestBody UserDto userDto) {
        log.debug("Received PATCH request to /users/{} endpoint with User-object {}", userId, userDto);
        return userClient.update(userId, userDto);
    }

    @GetMapping()
    public ResponseEntity<Object> findAllUsers() {
        log.debug("Received GET request to /users endpoint");
        return userClient.findAll();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> findUserById(@PathVariable Long userId) {
        log.debug("Received GET request to /users/{} endpoint", userId);
        return userClient.findById(userId);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUserById(@PathVariable Long userId) {
        log.debug("Received DELETE request to /users/{} endpoint", userId);
        userClient.delete(userId);
        return ResponseEntity.ok().build();
    }

}

