package ru.practicum.shareit.item.model;

import lombok.Data;

@Data
public class Item {

    private long id;
    private String name;
    private String description;
    private boolean available;
    private String owner;
    private String request;

    /*id — уникальный идентификатор вещи;
    name — краткое название;
    description — развёрнутое описание;
    available — статус о том, доступна или нет вещь для аренды;
    owner — владелец вещи;
    request — если вещь была создана по запросу другого пользователя, то в этом
    поле будет храниться ссылка на соответствующий запрос.*/

}
