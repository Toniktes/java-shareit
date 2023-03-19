package ru.practicum.shareit.user.stotage;

import ru.practicum.shareit.user.model.User;

import java.util.HashMap;
import java.util.Map;

public class UserStorageImpl implements UserStorage {

    private final Map<Long, User> users = new HashMap<>();


    @Override
    public User addUser(User user) {
        return users.put(user.getId(), user);
    }

    @Override
    public User updateUser(User user) {
        return users.put(user.getId(), user);
    }

    @Override
    public User getUser(long id) {
        return users.get(id);
    }

    @Override
    public void deleteUser(long id) {
        users.remove(id);
    }
}
