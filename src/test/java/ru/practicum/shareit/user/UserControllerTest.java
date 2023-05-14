package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;
    @InjectMocks
    private UserController userController;
    private final ObjectMapper mapper = new ObjectMapper();
    private MockMvc mockMvc;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(userController)
                .build();

        userDto = new UserDto(
                1,
                "name",
                "email"
                );
    }

    @Test
    void addUser_whenInvoked_thenResponseStatusOkWithCreatedUserInBody() throws Exception {
        Mockito.when(userService.addUser(Mockito.any()))
                .thenReturn(userDto);

        mockMvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));
    }

    @Test
    void updateUser_whenInvoked_thenResponseStatusOkWithCreatedUserInBody() throws Exception {
        Mockito.when(userService.updateUser(Mockito.any()))
                .thenReturn(userDto);

        mockMvc.perform(patch("/users/{userId}", userDto.getId())
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));
    }

    @Test
    void getAllUsers_WhenInvoked_ThenResponseStatusOkWithUsersCollectionInBody() throws Exception {
        List<UserDto> expectedUsers = List.of(userDto);
        Mockito.when(userService.getAllUsers()).thenReturn(expectedUsers);

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(userService.getAllUsers())));
    }

    @Test
    void getUser_WhenInvoked_ThenResponseStatusOkWithUserInBody() throws Exception {
        Mockito.when(userService.getUser(Mockito.anyLong())).thenReturn(userDto);

        mockMvc.perform(get("/users/{userId}", userDto.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(userService.getUser(userDto.getId()))))
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));
    }

    @Test
    void deleteUser_WhenInvoked_ThenResponseStatusOk() throws Exception {
        mockMvc.perform(delete("/users/{userId}", userDto.getId()))
                .andExpect(status().isOk());
    }
}