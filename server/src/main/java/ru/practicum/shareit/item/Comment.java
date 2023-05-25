package ru.practicum.shareit.item;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "text")
    private String text;
    @Column(name = "item", nullable = false)
    private long item;
    @Column(name = "author", nullable = false)
    private String authorName;
    @Column(name = "created", nullable = false)
    private LocalDateTime created;
}
