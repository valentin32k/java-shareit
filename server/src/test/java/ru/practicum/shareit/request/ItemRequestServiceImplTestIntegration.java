package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class ItemRequestServiceImplTestIntegration {
    private final EntityManager em;
    private final ItemRequestService service;
    private final UserService userService;
    private ItemRequest itemRequest;

    @BeforeEach
    void setup() {
        itemRequest = initRequest();
        itemRequest = service.createItemRequest(itemRequest, itemRequest.getRequestor().getId());
    }

    @Test
    void createItemRequest() {
        TypedQuery<ItemRequest> query =
                em.createQuery("select r from ItemRequest r where r.id = :id", ItemRequest.class);
        ItemRequest newItemRequest = query.setParameter("id", itemRequest.getId()).getSingleResult();

        assertThat(newItemRequest.getId(), notNullValue());
        assertThat(newItemRequest.getDescription(), equalTo(itemRequest.getDescription()));
        assertThat(newItemRequest.getRequestor().getId(), equalTo(itemRequest.getRequestor().getId()));
        assertThat(newItemRequest.getCreated(), equalTo(itemRequest.getCreated()));
    }

    @Test
    void getUserItemRequests() {
        List<ItemRequest> itemRequestList = service.getUserItemRequests(itemRequest.getRequestor().getId());
        ItemRequest newItemRequest = itemRequestList.get(0);

        assertThat(itemRequestList.size(), equalTo(1));
        assertThat(newItemRequest.getId(), notNullValue());
        assertThat(newItemRequest.getDescription(), equalTo(itemRequest.getDescription()));
        assertThat(newItemRequest.getRequestor().getId(), equalTo(itemRequest.getRequestor().getId()));
        assertThat(newItemRequest.getCreated(), equalTo(itemRequest.getCreated()));
    }

    @Test
    void getItemRequests() {
        List<ItemRequest> itemRequestList =
                service.getItemRequests(0, 20, itemRequest.getRequestor().getId() + 1);
        ItemRequest newItemRequest = itemRequestList.get(0);

        assertThat(itemRequestList.size(), equalTo(1));
        assertThat(newItemRequest.getId(), notNullValue());
        assertThat(newItemRequest.getDescription(), equalTo(itemRequest.getDescription()));
        assertThat(newItemRequest.getRequestor().getId(), equalTo(itemRequest.getRequestor().getId()));
        assertThat(newItemRequest.getCreated(), equalTo(itemRequest.getCreated()));
    }

    @Test
    void getItemRequestById() {
        ItemRequest newItemRequest = service.getItemRequestById(itemRequest.getId(), itemRequest.getRequestor().getId());

        assertThat(newItemRequest.getId(), notNullValue());
        assertThat(newItemRequest.getDescription(), equalTo(itemRequest.getDescription()));
        assertThat(newItemRequest.getRequestor().getId(), equalTo(itemRequest.getRequestor().getId()));
        assertThat(newItemRequest.getCreated(), equalTo(itemRequest.getCreated()));
    }

    private User initRequestor() {
        return User.builder()
                .name("Name")
                .email("emai11l@f.ru")
                .build();
    }

    private ItemRequest initRequest() {
        User requestor = userService.createUser(initRequestor());
        return ItemRequest.builder()
                .description("Description")
                .requestor(requestor)
                .created(LocalDateTime.now())
                .build();
    }
}