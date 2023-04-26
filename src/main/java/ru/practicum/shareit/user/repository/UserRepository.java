package ru.practicum.shareit.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.user.UserShort;
import ru.practicum.shareit.user.User;

public interface UserRepository extends JpaRepository<User, Long> {
    UserShort findByNameAndId(String name, long id);
}
