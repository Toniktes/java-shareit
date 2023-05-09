package ru.practicum.shareit.user.dto;


import lombok.*;

@Getter
@Setter
@EqualsAndHashCode
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    private long id;

    private String name;

    private String email;
}
