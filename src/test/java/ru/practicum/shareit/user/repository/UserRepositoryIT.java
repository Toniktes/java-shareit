package ru.practicum.shareit.user.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserShort;

import static org.junit.jupiter.api.Assertions.assertEquals;


@DataJpaTest
class UserRepositoryIT {

    @BeforeEach
    void beforeEach() {
        userRepository.save(new User(1, "name", "yan@mail.ru"));
    }

    @AfterEach
    void afterEach() {
        userRepository.deleteAll();
    }

    @Autowired
    private UserRepository userRepository;

    @Test
    void findByNameAndId() {
        UserShort actualUser = userRepository.findByNameAndId("name", 1);
        assertEquals(1, actualUser.getId());
    }
}