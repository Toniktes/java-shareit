package ru.practicum.shareit.user.stotage;

import ru.practicum.shareit.user.User;

import java.util.List;

public interface UserStorage {

    User addUser(User user);

    User updateUser(User user);

    User getUser(long id);

    void deleteUser(long id);

    List<User> getAllUsers();
}
