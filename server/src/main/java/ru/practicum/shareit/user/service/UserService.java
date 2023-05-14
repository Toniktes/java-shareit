package ru.practicum.shareit.user.service;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    @Transactional
    UserDto addUser(UserDto userDto);

    @Transactional
    UserDto updateUser(UserDto userDto);

    @Transactional
    UserDto getUser(long id);

    @Transactional
    void deleteUser(long id);

    @Transactional
    List<UserDto> getAllUsers();

}
