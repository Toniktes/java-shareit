package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;

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

    @PutMapping
    public User updateUser(@RequestBody @Valid User user) {
        log.debug("received a request to update User id: {}", user.getId());
        return userService.updateUser(user);
    }

    @GetMapping
    public User getUser(long id) {
        log.debug("received a request to get User id: {}", id);
        return userService.getUser(id);
    }

    @DeleteMapping
    public void deleteUser(long id) {
        log.debug("received a request to deleting User id: {}", id);
        userService.deleteUser(id);
    }


}
