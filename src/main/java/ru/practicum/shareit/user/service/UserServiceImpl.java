package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.stotage.UserStorage;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;

    @Override
    public User addUser(User user) {
        return userStorage.addUser(user);
    }

    @Override
    public User updateUser(User user) {
        return userStorage.updateUser(user);
    }

    @Override
    public User getUser(long id) {
        return userStorage.getUser(id);
    }

    @Override
    public void deleteUser(long id) {
        userStorage.deleteUser(id);
    }

}
