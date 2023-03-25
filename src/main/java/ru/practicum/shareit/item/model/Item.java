package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.request.ItemRequest;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Item {

    private long id;
    private String name;
    private String description;
    private Boolean available;
    private long owner;
    private ItemRequest request;


    /*id — уникальный идентификатор вещи;
    name — краткое название;
    description — развёрнутое описание;
    available — статус о том, доступна или нет вещь для аренды;
    owner — владелец вещи;
    request — если вещь была создана по запросу другого пользователя, то в этом
    поле будет храниться ссылка на соответствующий запрос.*/

}
