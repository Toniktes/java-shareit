package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.MapperUser;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
@ActiveProfiles("test")
@Sql(scripts = {"file:src/main/resources/schema.sql"})
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class UserServiceImplTest {
    private final UserRepository userRepository;
    private final UserServiceImpl userService;
    private User user;

    @BeforeEach
    public void setUp() {
        user = userRepository.save(User.builder()
                .name("name")
                .email("yan@mail.ru")
                .build());
    }

    @AfterEach
    public void afterEach() {
        userRepository.deleteAll();
    }

    @Test
    void addUser_whenValid_thenSaveAndReturnUser() {
        UserDto userDto = userService.addUser(MapperUser.userToDto(user));

        assertEquals(userRepository.getById(userDto.getId()), MapperUser.dtoToUser(userDto));
    }

    @Test
    void addUser_whenNotValid_thenThrowException() {
        user.setEmail("");

        assertThrows(ValidationException.class, () -> userService.addUser(MapperUser.userToDto(user)));
    }

    @Test
    void updateUser_whenValid_thenUpdateAndReturnUser() {
        user.setName("newName");
        UserDto userDto = userService.updateUser(MapperUser.userToDto(user));

        assertEquals(userRepository.getById(userDto.getId()), MapperUser.dtoToUser(userDto));
    }

    @Test
    void updateUser_whenNameIsNull_thenUpdateAndReturnUser() {
        UserDto userNew = UserDto.builder()
                .id(user.getId())
                .email("yan3@mail.ru")
                .build();
        UserDto userDto = userService.updateUser(userNew);

        assertEquals(userRepository.getById(userDto.getId()), MapperUser.dtoToUser(userDto));
    }

    @Test
    void updateUser_whenEmailIsNull_thenUpdateAndReturnUser() {
        UserDto userNew = UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .build();
        UserDto userDto = userService.updateUser(userNew);

        assertEquals(userRepository.getById(userDto.getId()), MapperUser.dtoToUser(userDto));
    }

    @Test
    void getUser_whenInvoked_thenReturnUserDto() {
        UserDto userDto = userService.getUser(user.getId());

        assertEquals(userRepository.getById(userDto.getId()), MapperUser.dtoToUser(userDto));
    }

    @Test
    void getAllUsers_whenInvoked_thenReturnListUserDto() {
        List<UserDto> userDtoList = userService.getAllUsers();

        assertEquals(List.of(MapperUser.userToDto(user)), userDtoList);
    }

    @Test
    void deleteUser_whenInvoked_thenDeleteUser() {
        userService.deleteUser(user.getId());

        assertEquals(Optional.empty(), userRepository.findById(user.getId()));
    }


}