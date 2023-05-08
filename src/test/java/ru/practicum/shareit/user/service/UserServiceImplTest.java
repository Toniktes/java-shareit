package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.MapperUser;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    private UserDto userDto;

    @BeforeEach
    void setUp() {
        userDto = new UserDto(
                1,
                "name",
                "yan@email"
        );
    }

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void addUser_whenValidUser_thenReturnUser() {
        when(userRepository.save(Mockito.any()))
                .thenReturn(MapperUser.dtoToUser(userDto));
        UserDto actualUser = userService.addUser(userDto);

        assertEquals(userDto, actualUser);
        verify(userRepository).save(MapperUser.dtoToUser(userDto));
    }

    @Test
    void addUser_whenNotValidUser_thenThrowException() {
        when(userRepository.save(Mockito.any())).thenThrow(ValidationException.class);

        assertThrows(ValidationException.class, () -> userService.addUser(userDto));
    }

    @Test
    void updateUser_whenValidUser_thenReturnUser() {
        when(userRepository.save(Mockito.any()))
                .thenReturn(MapperUser.dtoToUser(userDto));
        UserDto actualUser = userService.updateUser(userDto);

        assertEquals(userDto, actualUser);
        verify(userRepository).save(MapperUser.dtoToUser(userDto));
    }

    @Test
    void updateUser_whenNotValidUser_thenReturnUser() {
        when(userRepository.save(Mockito.any())).thenThrow(ValidationException.class);

        assertThrows(ValidationException.class, () -> userService.addUser(userDto));
    }

    @Test
    void getUser_whenUserFound_thenReturnUser() {
        User user = new User(
                1,
                "name",
                "yan@email");
        when(userRepository.getById(Mockito.anyLong())).thenReturn(user);

        UserDto actualUser = userService.getUser(userDto.getId());

        assertEquals(userDto, actualUser);
    }


    @Test
    void getUser_whenUserNotFound_thenThrowException() {
        when(userRepository.getById(Mockito.anyLong())).thenThrow(NotFoundException.class);

        assertThrows(NotFoundException.class, () -> userService.getUser(userDto.getId()));
    }

    @Test
    void getAllUsers_whenInvoked_thenReturnListOfUsers() {
        User user = new User(
                1,
                "name",
                "yan@email");
        List<UserDto> userList = List.of(userDto);
        when(userRepository.findAll()).thenReturn(List.of(user));

        assertEquals(userList, userService.getAllUsers());
    }

    @Test
    void deleteUser_whenInvoked() {
        doNothing().when(userRepository).deleteById(Mockito.anyLong());
        userService.deleteUser(userDto.getId());
        verify(userRepository, times(1)).deleteById(Mockito.anyLong());

    }
}