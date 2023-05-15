package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.Comment;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Map;

@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }


    public ResponseEntity<Object> addItem(ItemDto itemDto, long userId) {
        return post("", userId, itemDto);
    }

    public ResponseEntity<Object> updateItem(ItemDto itemDto, long itemId, long userId) {
        return patch("/" + itemId, userId, itemDto);
    }

    public ResponseEntity<Object> getItemDtoWithBooking(long itemId, long userId) {
        return get("/" + itemId, userId);
    }

    public ResponseEntity<Object> getListOfThings(long userId, Pageable pageable) {
        Map<String, Object> parameters = Map.of(
                "from", pageable.getPageNumber(),
                "size", pageable.getPageSize()
        );
        return get("?from={from}&size={size}", userId);
    }

    public ResponseEntity<Object> getThingsForSearch(String text, long userId, Pageable pageable) {
        Map<String, Object> parameters = Map.of(
                "text", text,
                "from", pageable.getPageNumber(),
                "size", pageable.getPageSize()
        );
        return get("/search?text={text}&from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> addComment(Comment comment, long userId, long itemId) {
        return post("/" + itemId + "/comment", userId, comment);
    }
}
