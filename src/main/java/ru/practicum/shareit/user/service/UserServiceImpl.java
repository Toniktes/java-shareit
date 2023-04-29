package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.MapperUser;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public UserDto addUser(UserDto userDto) {
        validate(userDto);
        User user = userRepository.save(MapperUser.dtoToUser(userDto));
        return MapperUser.userToDto(user);
    }

    private void validate(UserDto userDto) {
        if (userDto.getEmail() == null || userDto.getEmail().isBlank() || !userDto.getEmail().contains("@")) {
            throw new ValidationException("Email can't be empty and must contains @");
        }
    }

    @Transactional
    @Override
    public UserDto updateUser(UserDto userDto) {
        validateUpdate(userDto);
        User user = userRepository.save(MapperUser.dtoToUser(userDto));
        return MapperUser.userToDto(user);
    }

    private void validateUpdate(UserDto userDto) {
        if (userDto.getName() == null) {
            userDto.setName(getUser(userDto.getId()).getName());
        }
        if (userDto.getEmail() == null) {
            userDto.setEmail(getUser(userDto.getId()).getEmail());
        }
    }

    @Transactional(readOnly = true)
    @Override
    public UserDto getUser(long id) {
        return MapperUser.userToDto(userRepository.getById(id));
    }

    @Transactional(readOnly = true)
    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(MapperUser::userToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public void deleteUser(long id) {
        userRepository.deleteById(id);
    }

}
