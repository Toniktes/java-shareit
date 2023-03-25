package ru.practicum.shareit.request;


import lombok.Data;

import java.time.Instant;

@Data
public class ItemRequest {

    private long id;
    private String description;
    private long requestor;
    private Instant created;

    /*id — уникальный идентификатор запроса;
    description — текст запроса, содержащий описание требуемой вещи;
    requestor — пользователь, создавший запрос;
    created — дата и время создания запроса.*/

}
