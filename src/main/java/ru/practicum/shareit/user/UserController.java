package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public User addUser(@RequestBody @Valid User user) {
        log.debug("received a request to add User");
        return userService.addUser(user);
    }

    @PatchMapping("/{userId}")
    public User updateUser(@PathVariable long userId, @RequestBody User user) {
        log.debug("received a request to update User id: {}", userId);
        user.setId(userId);
        return userService.updateUser(user);
    }

    @GetMapping("/{userId}")
    public User getUser(@PathVariable long userId) {
        log.debug("received a request to get User id: {}", userId);
        return userService.getUser(userId);
    }

    @GetMapping
    public List<User> getAllUsers() {
        log.debug("received a request to get all Users");
        return userService.getAllUsers();
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable long userId) {
        log.debug("received a request to deleting User id: {}", userId);
        userService.deleteUser(userId);
    }


}
