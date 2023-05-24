package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import ru.practicum.shareit.exceptions.BadMethodArgumentsException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
    void confirmBooking_shouldReturnBookingWithApprovedStatus() {
        Booking booking = initBooking();
        Mockito.when(bookingRepository.findById(any())).thenReturn(Optional.of(booking));

        assertEquals(BookingStatus.APPROVED, service.confirmBooking(1, 1, true).getStatus());
        verify(bookingRepository, times(1)).findById(any());
    }

    @Test
    void confirmBooking_shouldThrowNotFoundException() {
        Booking booking = initBooking();
        booking.getBooker().setId(2);
        Mockito.when(bookingRepository.findById(any())).thenReturn(Optional.of(booking));

        NotFoundException exception = assertThrows(
                NotFoundException.class, () -> service.confirmBooking(1, 2, true));

        assertEquals("Booker with id = 2 try to confirm booking", exception.getMessage());
    }

    @Test
    void getBookingById_shouldReturnBookingById() {
        Booking booking = initBooking();
        Mockito.when(bookingRepository.findById(any())).thenReturn(Optional.of(booking));

        assertEquals(booking, service.getBookingById(1, 1));
        verify(bookingRepository, times(1)).findById(any());
    }

    @Test
    void getBookingById_shouldThrowNotFoundException() {
        Mockito.when(bookingRepository.findById(any())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class, () -> service.getBookingById(1, 2));

        assertEquals("Can not find booking with id = 1", exception.getMessage());
    }

    @Test
    void getBookingsByUserId_shouldReturnListOfUserBookings() {
        Booking booking = initBooking();
        Mockito.when(userRepository.findById(anyLong())).thenReturn(Optional.of(initUser()));
        Mockito.when(bookingRepository.findAllByBookerId(anyLong(), any())).thenReturn(new PageImpl<>(List.of(booking)));

        assertEquals(List.of(booking), service.getBookingsByUserId(BookingState.ALL, 0, 20, 1));
        verify(bookingRepository, times(1)).findAllByBookerId(anyLong(), any());
        verify(userRepository, times(1)).findById(anyLong());
    }

    @Test
    void getBookingsByUserId_shouldThrowBadMethodArgumentsException() {
        Mockito.when(userRepository.findById(anyLong())).thenReturn(Optional.of(initUser()));

        BadMethodArgumentsException exception = assertThrows(
                BadMethodArgumentsException.class, () -> service.getBookingsByUserId(BookingState.ALL, 1, 0, 1));

        assertEquals("Bookings size must not be less than 1 and start item number must not be less then 0", exception.getMessage());
    }

    @Test
    void getOwnerItemsBookings_shouldReturnListOfOwnerItemBookings() {
        Booking booking = initBooking();
        Mockito.when(userRepository.findById(anyLong())).thenReturn(Optional.of(initUser()));
        Mockito.when(bookingRepository.findAllByItemOwnerId(anyLong(), any())).thenReturn(new PageImpl<>(List.of(booking)));

        assertEquals(List.of(booking), service.getOwnerItemsBookings(BookingState.ALL, 0, 20, 1));
        verify(bookingRepository, times(1)).findAllByItemOwnerId(anyLong(), any());
        verify(userRepository, times(1)).findById(anyLong());
    }

    @Test
    void getOwnerItemsBookings_shouldThrowBadMethodArgumentsException() {
        Mockito.when(userRepository.findById(anyLong())).thenReturn(Optional.of(initUser()));

        BadMethodArgumentsException exception = assertThrows(
                BadMethodArgumentsException.class, () -> service.getOwnerItemsBookings(BookingState.ALL, 1, 0, 1));

        assertEquals("Bookings size must not be less than 1 and start item number must not be less then 0", exception.getMessage());
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