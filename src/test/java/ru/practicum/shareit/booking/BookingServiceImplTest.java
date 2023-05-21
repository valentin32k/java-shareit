package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exceptions.BadMethodArgumentsException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    BookingServiceImpl service;

    @Test
    void createBooking_shoulCreateNewBooking() {
        Booking booking = initBooking();
        Mockito.when(bookingRepository.save(any())).thenReturn(booking);

        assertEquals(booking, service.createBooking(booking));
        verify(bookingRepository, times(1)).save(any());
    }

    @Test
    void createBooking_shouldThrowNotFoundException() {
        Booking booking = initBooking();
        booking.setBooker(initUser());

        NotFoundException exception = assertThrows(
                NotFoundException.class, () -> service.createBooking(booking));
        assertEquals("The user with id = " +
                booking.getBooker().getId() +
                " is trying to book their item", exception.getMessage());
    }

    @Test
    void createBooking_shouldThrowBadMethodArgumentsException() {
        Booking booking = initBooking();
        booking.getItem().setAvailable(false);

        BadMethodArgumentsException exception = assertThrows(
                BadMethodArgumentsException.class, () -> service.createBooking(booking));
        assertEquals("Bad method arguments: available = " + booking.getItem().getAvailable() + ", start booking time = " + booking.getStart() + ", end booking time = " + booking.getEnd(), exception.getMessage());
    }





    @Test
    void confirmBooking() {
    }

    @Test
    void getBookingById() {
    }

    @Test
    void getBookingsByUserId() {
    }

    @Test
    void getOwnerItemsBookings() {
    }

    private Booking initBooking() {
        User booker = initUser();
        booker.setId(5);
        return Booking.builder()
                .id(1)
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusHours(5))
                .item(initItem())
                .booker(booker)
                .status(BookingStatus.WAITING)
                .build();
    }

    private Item initItem() {
        return Item.builder()
                .id(1)
                .name("name")
                .description("descriiption")
                .available(true)
                .owner(initUser())
                .build();
    }

    private User initUser() {
        return User.builder()
                .id(1L)
                .name("Иван")
                .email("vanya@email.ru")
                .build();
    }
}