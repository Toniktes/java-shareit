package ru.practicum.shareit.item;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "items")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;
    @Column(name = "text")
    String text;
    @Column(name = "item")
    long item;
    @Column(name = "author")
    long author;
    @Column(name = "created")
    LocalDateTime created;
}
