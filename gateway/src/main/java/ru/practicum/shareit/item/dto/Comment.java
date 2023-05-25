package ru.practicum.shareit.item.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class Comment {
    private long id;
    private String text;
    private long item;
    private String authorName;
    private LocalDateTime created;
}
