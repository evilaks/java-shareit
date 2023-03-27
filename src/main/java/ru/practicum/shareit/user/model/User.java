package ru.practicum.shareit.user.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

/**
 * TODO Sprint add-controllers.
 */

@Data
@Builder
public class User {
    private Long id;
    @NotBlank
    private String name;
    @Email
    private String email;
}

/*
    todo

    Сделать преобразование в dto (хз, зачем, может потом пригодится)

    Сделать мапперы
 */
