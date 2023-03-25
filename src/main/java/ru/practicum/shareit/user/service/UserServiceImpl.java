package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.stotage.UserStorage;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {
    private int generateId = 0;
    private final UserStorage userStorage;
    private final List<String> emails = new ArrayList<>();

    @Override
    public User addUser(User user) {
        validate(user);
        emails.add(user.getEmail());
        return userStorage.addUser(user);
    }

    @Override
    public User updateUser(User user) {
        validateUpdate(user);
        emails.add(user.getEmail());
        return userStorage.updateUser(user);
    }

    private void validateUpdate(User user) {
        if (user.getName() == null) {
            user.setName(getUser(user.getId()).getName());
        }
        if (user.getEmail() != null) {
            emails.remove(getUser(user.getId()).getEmail());
        } else {
            user.setEmail(getUser(user.getId()).getEmail());
            emails.remove(user.getEmail());
        }
        if (emails.contains(user.getEmail())) {
            throw new RuntimeException("Duplicate email");
        }
    }

    @Override
    public User getUser(long id) {
        return userStorage.getUser(id);
    }

    @Override
    public List<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    @Override
    public void deleteUser(long id) {
        emails.remove(getUser(id).getEmail());
        userStorage.deleteUser(id);
    }

    private void validate(User user) {
        if (emails.contains(user.getEmail())) {
            throw new RuntimeException("Duplicate email");
        }
        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            throw new ValidationException("Email can't be empty and must contains @");
        }

        setId(user);
    }

    private void setId(User user) {
        if (user.getId() == 0) {
            user.setId(++generateId);
        }
    }

}
