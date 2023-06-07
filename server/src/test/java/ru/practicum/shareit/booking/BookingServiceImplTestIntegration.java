package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemService;
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
class BookingServiceImplTestIntegration {
    private final EntityManager em;
    private final ItemService itemService;
    private final UserService userService;
    private final BookingServiceImpl bookingService;
    private Booking booking;

    @BeforeEach
    void setup() {
        booking = bookingService.createBooking(initBooking());
    }

    @Test
    void createBooking() {
        TypedQuery<Booking> query = em.createQuery("select b from Booking b where b.id = :id", Booking.class);
        Booking newBooking = query.setParameter("id", booking.getId()).getSingleResult();

        assertThat(newBooking.getId(), notNullValue());
        assertThat(newBooking.getStart(), equalTo(booking.getStart()));
        assertThat(newBooking.getEnd(), equalTo(booking.getEnd()));
        assertThat(newBooking.getStatus(), equalTo(booking.getStatus()));
        assertThat(newBooking.getItem().getId(), equalTo(booking.getItem().getId()));
        assertThat(newBooking.getBooker().getId(), equalTo(booking.getBooker().getId()));
    }

    @Test
    void confirmBooking() {
        bookingService.confirmBooking(booking.getId(), booking.getItem().getOwner().getId(), true);

        TypedQuery<Booking> query = em.createQuery("select b from Booking b where b.id = :id", Booking.class);
        Booking newBooking = query.setParameter("id", booking.getId()).getSingleResult();

        assertThat(newBooking.getStatus(), equalTo(BookingStatus.APPROVED));
    }

    @Test
    void getBookingById() {
        Booking returnedBooking = bookingService.getBookingById(booking.getId(), booking.getBooker().getId());

        assertThat(returnedBooking.getId(), notNullValue());
        assertThat(returnedBooking.getStart(), equalTo(booking.getStart()));
        assertThat(returnedBooking.getEnd(), equalTo(booking.getEnd()));
        assertThat(returnedBooking.getStatus(), equalTo(booking.getStatus()));
        assertThat(returnedBooking.getItem().getId(), equalTo(booking.getItem().getId()));
        assertThat(returnedBooking.getBooker().getId(), equalTo(booking.getBooker().getId()));
    }

    @Test
    void getBookingsByUserId() {
        List<Booking> returnedBookings = bookingService.getBookingsByUserId(BookingState.ALL, 0, 20, booking.getBooker().getId());
        Booking returnedBooking = returnedBookings.get(0);

        assertThat(returnedBookings.size(), equalTo(1));
        assertThat(returnedBooking.getId(), notNullValue());
        assertThat(returnedBooking.getStart(), equalTo(booking.getStart()));
        assertThat(returnedBooking.getEnd(), equalTo(booking.getEnd()));
        assertThat(returnedBooking.getStatus(), equalTo(booking.getStatus()));
        assertThat(returnedBooking.getItem().getId(), equalTo(booking.getItem().getId()));
        assertThat(returnedBooking.getBooker().getId(), equalTo(booking.getBooker().getId()));
    }

    @Test
    void getOwnerItemsBookings() {
        List<Booking> returnedBookings = bookingService.getOwnerItemsBookings(BookingState.ALL, 0, 20, booking.getItem().getOwner().getId());
        Booking returnedBooking = returnedBookings.get(0);

        assertThat(returnedBookings.size(), equalTo(1));
        assertThat(returnedBooking.getId(), notNullValue());
        assertThat(returnedBooking.getStart(), equalTo(booking.getStart()));
        assertThat(returnedBooking.getEnd(), equalTo(booking.getEnd()));
        assertThat(returnedBooking.getStatus(), equalTo(booking.getStatus()));
        assertThat(returnedBooking.getItem().getId(), equalTo(booking.getItem().getId()));
        assertThat(returnedBooking.getBooker().getId(), equalTo(booking.getBooker().getId()));
    }

    private User initUser() {
        return User.builder()
                .name("Name")
                .email("email@f.ru")
                .build();
    }

    private User initOwner() {
        return User.builder()
                .name("Name")
                .email("emai11l@f.ru")
                .build();
    }

    private Item initItem(User owner) {
        return Item.builder()
                .id(1)
                .name("aaa")
                .description("ddd")
                .available(true)
                .owner(owner)
                .build();
    }

    private Booking initBooking() {
        User itemOwner = userService.createUser(initOwner());
        User booker = userService.createUser(initUser());
        Item item = itemService.createItem(initItem(itemOwner), itemOwner.getId(), 0);
        return Booking.builder()
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusHours(5))
                .booker(booker)
                .item(item)
                .status(BookingStatus.WAITING)
                .build();
    }
}