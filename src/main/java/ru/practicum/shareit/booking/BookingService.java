package ru.practicum.shareit.booking;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingService {

    /**
     * Creates a new booking
     * If the owner try to book his item throws NotFoundException
     * If the booking data is incorrect throws BadMethodArgumentsException
     *
     * @param booking
     * @return new booking
     */
    Booking createBooking(Booking booking);

    /**
     * Confirm the booking
     * If not item owner try to confirm booking
     * or owner try to change booking confirm status
     * throws BadMethodArgumentsException
     *
     * @param bookingId, ownerId, approved
     * @return booking
     */
    Booking confirmBooking(long bookingId, long ownerId, Boolean approved);

    /**
     * Get the booking info by id
     * If the requested booking is not available
     * or the owner is not yet trying to get information about it
     * throws NotFoundException
     *
     * @param bookingId, askUserId
     * @return booking
     */
    Booking getBookingById(long bookingId, long askUserId);

    /**
     * Get last booking info for item with itemId
     *
     * @param itemId, currentDate
     * @return booking
     */
    Booking getLastBooking(long itemId, LocalDateTime currentDate);

    /**
     * Get next booking info for item with itemId
     *
     * @param itemId, currentDate
     * @return booking
     */
    Booking getNextBooking(long itemId, LocalDateTime currentDate);

    /**
     * Returns bookings by user id
     * If there is no user with id throws BadUserException
     *
     * @param state, userId
     * @return List of bookings
     */
    List<Booking> getBookingsByUserId(BookingState state, long userId);

    /**
     * Returns owner items bookings by owner id
     * If there is no user with id throws BadUserException
     *
     * @param state, userId
     * @return List of bookings
     */
    List<Booking> getOwnerItemsBookings(BookingState state, long userId);

    /**
     * Get user bookings by user id
     * If there is no user with id throws BadUserException
     *
     * @param userId
     * @return List of bookings
     */
    List<Booking> getOwnerItemsSortedById(long userId);

    /**
     * Returns the conclusion about the ability to comment
     *
     * @param userId, itemId
     * @return conclusion about the ability to comment
     */
    boolean isAllowedToComment(long userId, long itemId);
}
