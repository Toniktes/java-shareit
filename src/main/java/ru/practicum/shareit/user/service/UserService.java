package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {
    User addUser(User user);

    User updateUser(User user);

    User getUser(long id);

    void deleteUser(long id);

    List<User> getAllUsers();

}
