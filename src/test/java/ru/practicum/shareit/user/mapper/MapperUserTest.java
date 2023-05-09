package ru.practicum.shareit.user.mapper;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class MapperUserTest {

    private User user;
    private UserDto userDto;

    @BeforeEach
    public void setUp() {
        user = User.builder()
                .id(1)
                .name("name")
                .email("yan@mail.ru")
                .build();
        userDto = new UserDto(
                1,
                "name",
                "yan@mail.ru"
        );
    }

    @Test
    void dtoToUser() {
        assertEquals(user, MapperUser.dtoToUser(userDto));
    }

    @Test
    void userToDto() {
        assertEquals(userDto, MapperUser.userToDto(user));
    }
}