package ru.practicum.shareit.booking;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

public interface BookingService {
    /**
     * Creates a new booking
     *
     * @param bookingDto, bookerId
     * @return new booking
     */
    Booking createBooking(BookingDto bookingDto, long bookerId);

    /**
     * Confirm the booking
     *
     * @param bookingId, ownerId, approved
     * @return booking
     */
    Booking confirmBooking(long bookingId, long ownerId, Boolean approved);

    /**
     * Get the booking info by id
     *
     * @param bookingId, askUserId
     * @return booking
     */
    Booking getBookingById(long bookingId, long askUserId);

    /**
     * Get bookings by user id
     *
     * @param state, userId
     * @return List of bookings
     */
    List<Booking> getBookingsByUserId(BookingState state, long userId);

    /**
     * Get owner bookings by owner id
     *
     * @param state, userId
     * @return List of bookings
     */
    List<Booking> getOwnerItemsBookings(BookingState state, long userId);

    }
