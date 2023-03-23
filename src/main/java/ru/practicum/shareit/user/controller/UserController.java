package ru.practicum.shareit.user.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping(path = "/users")
public class UserController {
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
