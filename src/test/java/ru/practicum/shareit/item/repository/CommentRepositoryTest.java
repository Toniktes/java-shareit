package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.Comment;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
@DataJpaTest
class CommentRepositoryTest {
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRequestRepository itemRequestRepository;
    @Autowired
    private CommentRepository commentRepository;
    private ItemRequest itemRequest;
    private User user;
    private Item item;
    private Comment comment;

    @BeforeEach
    public void beforeEach() {
        user = new User();
        user.setName("user");
        user.setEmail("yan@mail.ru");
        user = userRepository.save(user);

        itemRequest = new ItemRequest();
        itemRequest.setDescription("des");
        itemRequest.setRequestor(user.getId());
        itemRequest.setCreated(LocalDateTime.now());
        itemRequest = itemRequestRepository.save(itemRequest);

        item = new Item();
        item.setName("name");
        item.setDescription("des");
        item.setAvailable(true);
        item.setOwner(user.getId());
        item.setRequestId(itemRequest.getId());
        item = itemRepository.save(item);

        comment = new Comment();
        comment.setText("comment");
        comment.setItem(item.getId());
        comment.setAuthorName("user");
        comment.setCreated(LocalDateTime.now());
        commentRepository.save(comment);
    }

    @Test
    void findByItem() {
        List<Comment> commentNew = commentRepository.findByItem(item.getId());

        assertEquals(List.of(comment), commentNew);
    }
}