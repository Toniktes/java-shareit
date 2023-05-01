package ru.practicum.shareit.request;


import lombok.Data;

import javax.persistence.*;
import java.time.Instant;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "requests")
public class ItemRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "description", nullable = false, length = 1000)
    private String description;
    @Column(name = "requestor", nullable = false)
    private long requestor;
    @Column(name = "created", nullable = false)
    private LocalDateTime created;

}
