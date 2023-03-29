package ru.practicum.shareit.request;


import lombok.Data;

import java.time.Instant;

@Data
public class ItemRequest {

    private long id;
    private String description;
    private long requestor;
    private Instant created;

}
