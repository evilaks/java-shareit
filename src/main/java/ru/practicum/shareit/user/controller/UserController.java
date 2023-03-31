package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;


@Slf4j
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping()
    public ResponseEntity<UserDto> addUser(@RequestBody @Valid UserDto userDto) {
        log.debug("Received POST request to /users endpoint with User-object {}", userDto);
        return ResponseEntity.ok().body(userService.add(userDto));
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<UserDto> updateUser(@PathVariable Long userId, @RequestBody UserDto userDto) {
        log.debug("Received PATCH request to /users/{} endpoint with User-object {}", userId, userDto);
        return ResponseEntity.ok().body(userService.update(userId, userDto));
    }

    @GetMapping()
    public ResponseEntity<List<UserDto>> findAllUsers() {
        log.debug("Received GET request to /users endpoint");
        return ResponseEntity.ok().body(userService.findAll());
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserDto> findUserById(@PathVariable Long userId) {
        log.debug("Received GET request to /users/{} endpoint", userId);
        return ResponseEntity.ok().body(userService.findById(userId));
    }

    @DeleteMapping("/{userId}")
    public void deleteUserById(@PathVariable Long userId) {
        log.debug("Received DELETE request to /users/{} endpoint", userId);
        userService.delete(userId);
    }

}

