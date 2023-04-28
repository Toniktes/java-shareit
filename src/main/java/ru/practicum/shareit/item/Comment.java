package ru.practicum.shareit.item;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;
    @Column(name = "text")
    String text;
    @Column(name = "item", nullable = false)
    long item;
    @Column(name = "author", nullable = false)
    String authorName;
    @Column(name = "created", nullable = false)
    LocalDateTime created;
}
