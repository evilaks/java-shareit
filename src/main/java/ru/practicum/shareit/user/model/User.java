package ru.practicum.shareit.user.model;

import lombok.Builder;
import lombok.Data;

/**
 * TODO Sprint add-controllers.
 */

@Data
@Builder
public class User {
    private Long id;
    private String name;
    private String email;
}

/*
    todo

    Сделать преобразование в dto (хз, зачем, может потом пригодится)

    Сделать мапперы
 */
