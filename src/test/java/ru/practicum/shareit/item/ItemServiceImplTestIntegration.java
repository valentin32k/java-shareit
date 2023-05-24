package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.BookingStatus;
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
class ItemServiceImplTestIntegration {
    private final EntityManager em;
    private final ItemService itemService;
    private final UserService userService;
    private final BookingService bookingService;

    @Test
    void createItem() {
        Item item = initItem();
        item = itemService.createItem(item, item.getOwner().getId(), 0);

        TypedQuery<Item> query = em.createQuery("select i from Item i where i.id = :id", Item.class);
        Item newItem = query.setParameter("id", item.getId()).getSingleResult();

        assertThat(newItem.getId(), notNullValue());
        assertThat(newItem.getName(), equalTo(item.getName()));
        assertThat(newItem.getDescription(), equalTo(item.getDescription()));
        assertThat(newItem.getAvailable(), equalTo(item.getAvailable()));
        assertThat(newItem.getOwner().getId(), equalTo(item.getOwner().getId()));
    }

    @Test
    void updateItem() {
        Item item = initItem();
        item = itemService.createItem(item, item.getOwner().getId(), 0);

        item.setName("cccccc");
        item.setDescription("ssssss");
        item.setAvailable(false);

        TypedQuery<Item> query = em.createQuery("select i from Item i where i.id = :id", Item.class);
        Item newItem = query.setParameter("id", item.getId()).getSingleResult();

        assertThat(newItem.getId(), notNullValue());
        assertThat(newItem.getName(), equalTo(item.getName()));
        assertThat(newItem.getDescription(), equalTo(item.getDescription()));
        assertThat(newItem.getAvailable(), equalTo(item.getAvailable()));
        assertThat(newItem.getOwner().getId(), equalTo(item.getOwner().getId()));
    }

    @Test
    void getItemById() {
        Item item = initItem();
        item = itemService.createItem(item, item.getOwner().getId(), 0);

        Item newItem = itemService.getItemById(item.getId(), item.getOwner().getId());

        assertThat(newItem.getId(), notNullValue());
        assertThat(newItem.getName(), equalTo(item.getName()));
        assertThat(newItem.getDescription(), equalTo(item.getDescription()));
        assertThat(newItem.getAvailable(), equalTo(item.getAvailable()));
        assertThat(newItem.getOwner().getId(), equalTo(item.getOwner().getId()));
    }

    @Test
    void getUserItems() {
        Item item = initItem();
        item = itemService.createItem(item, item.getOwner().getId(), 0);
        List<Item> items = itemService.getUserItems(item.getOwner().getId(), 0, 20);
        Item newItem = items.get(0);

        assertThat(items.size(), equalTo(1));
        assertThat(newItem.getId(), notNullValue());
        assertThat(newItem.getName(), equalTo(item.getName()));
        assertThat(newItem.getDescription(), equalTo(item.getDescription()));
        assertThat(newItem.getAvailable(), equalTo(item.getAvailable()));
        assertThat(newItem.getOwner().getId(), equalTo(item.getOwner().getId()));
    }

    @Test
    void findItemsWithText() {
        Item item = initItem();
        item = itemService.createItem(item, item.getOwner().getId(), 0);
        List<Item> items = itemService.findItemsWithText("dd", 0, 20);

        Item newItem = items.get(0);

        assertThat(items.size(), equalTo(1));
        assertThat(newItem.getId(), notNullValue());
        assertThat(newItem.getName(), equalTo(item.getName()));
        assertThat(newItem.getDescription(), equalTo(item.getDescription()));
        assertThat(newItem.getAvailable(), equalTo(item.getAvailable()));
        assertThat(newItem.getOwner().getId(), equalTo(item.getOwner().getId()));
    }

    @Test
    void createComment() {
        Item item = initItem();
        item = itemService.createItem(item, item.getOwner().getId(), item.getOwner().getId());
        User booker = initBooker();
        booker = userService.createUser(booker);
        Booking booking = Booking.builder()
                .start(LocalDateTime.now().minusHours(20))
                .end(LocalDateTime.now().minusHours(15))
                .booker(booker)
                .item(item)
                .status(BookingStatus.APPROVED)
                .build();
        bookingService.createBooking(booking);
        Comment comment = Comment.builder()
                .text("dcsdcsdc")
                .item(item)
                .author(booker)
                .build();
        comment = itemService.createComment(comment, item.getId(), booker.getId());

        TypedQuery<Comment> query = em.createQuery("select c from Comment c where c.id = :id", Comment.class);
        Comment newComment = query.setParameter("id", item.getId()).getSingleResult();

        assertThat(newComment.getId(), notNullValue());
        assertThat(newComment.getText(), equalTo(comment.getText()));
        assertThat(newComment.getItem().getId(), equalTo(comment.getItem().getId()));
        assertThat(newComment.getAuthor().getId(), equalTo(comment.getAuthor().getId()));
        assertThat(newComment.getCreated(), equalTo(comment.getCreated()));
    }

    @Test
    void getItemComments() {
        Item item = initItem();
        item = itemService.createItem(item, item.getOwner().getId(), item.getOwner().getId());
        User booker = initBooker();
        booker = userService.createUser(booker);
        Booking booking = Booking.builder()
                .start(LocalDateTime.now().minusHours(20))
                .end(LocalDateTime.now().minusHours(15))
                .booker(booker)
                .item(item)
                .status(BookingStatus.APPROVED)
                .build();
        bookingService.createBooking(booking);
        Comment comment = Comment.builder()
                .text("dcsdcsdc")
                .item(item)
                .author(booker)
                .build();
        comment = itemService.createComment(comment, item.getId(), booker.getId());

        List<Comment> commentList = itemService.getItemComments(item.getId());
        Comment newComment = commentList.get(0);

        assertThat(commentList.size(), equalTo(1));
        assertThat(newComment.getId(), notNullValue());
        assertThat(newComment.getText(), equalTo(comment.getText()));
        assertThat(newComment.getItem().getId(), equalTo(comment.getItem().getId()));
        assertThat(newComment.getAuthor().getId(), equalTo(comment.getAuthor().getId()));
        assertThat(newComment.getCreated(), equalTo(comment.getCreated()));
    }

    private User initOwner() {
        return User.builder()
                .name("Name")
                .email("emai11l@f.ru")
                .build();
    }

    private User initBooker() {
        return User.builder()
                .name("Name22")
                .email("emai11l2@f.ru")
                .build();
    }

    private Item initItem() {
        User owner = userService.createUser(initOwner());
        return Item.builder()
                .id(1)
                .name("aaa")
                .description("ddd")
                .available(true)
                .owner(owner)
                .build();
    }
}