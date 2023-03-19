package ru.practicum.shareit.user.model;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
public class User {

    private long id;
    @NotBlank
    private String name;
    @Email
    private String email;
}
