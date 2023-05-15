package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.BadMethodArgumentsException;
import ru.practicum.shareit.exceptions.BadUserException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserService userService;

    @Override
    @Transactional
    public Booking createBooking(Booking booking) {
        if (booking.getItem().getOwner().getId() == booking.getBooker().getId()) {
            throw new NotFoundException("The user with id = " + booking.getBooker().getId() + " is trying to book their item");
        }
        if (!booking.getItem().getAvailable() ||
                !booking.getEnd().isAfter(booking.getStart())) {
            throw new BadMethodArgumentsException("Bad method arguments: available = " +
                    booking.getItem().getAvailable() +
                    ", start booking time = " +
                    booking.getStart() +
                    ", end booking time = " +
                    booking.getEnd());
        }
        return bookingRepository.save(booking);
    }

    @Override
    @Transactional
    public Booking confirmBooking(long bookingId, long ownerId, Boolean approved) {
        Booking returnedBooking = getBookingById(bookingId, ownerId);
        if (ownerId != returnedBooking.getItem().getOwner().getId() && approved != null) {
            if (ownerId == returnedBooking.getBooker().getId()) {
                throw new NotFoundException("Booker with id = " + ownerId + " try to confirm booking");
            } else {
                throw new BadMethodArgumentsException("Not item owner try to confirm booking");
            }
        }
        if (BookingStatus.APPROVED == returnedBooking.getStatus() ||
                BookingStatus.REJECTED == returnedBooking.getStatus()) {
            throw new BadMethodArgumentsException("You can not change booking status");
        }
        if (approved) {
            returnedBooking.setStatus(BookingStatus.APPROVED);
        } else {
            returnedBooking.setStatus(BookingStatus.REJECTED);
        }
        return returnedBooking;
    }

    @Override
    public Booking getBookingById(long bookingId, long askUserId) {
        Booking returnedBooking = bookingRepository
                .findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Can not find booking with id = " + bookingId));
        if (askUserId != returnedBooking.getBooker().getId() &&
                askUserId != returnedBooking.getItem().getOwner().getId()) {
            throw new NotFoundException("Not item owner and not booker try to get booking");
        }
        return returnedBooking;
    }

    @Override
    public Booking getLastBooking(long itemId) {
        return bookingRepository.findFirstByItemIdAndStartIsBeforeAndStatusOrderByStartDesc(
                itemId,
                LocalDateTime.now(),
                BookingStatus.APPROVED);
    }

    @Override
    public Booking getNextBooking(long itemId) {
        return bookingRepository.findFirstByItemIdAndStartIsAfterAndStatusOrderByStartAsc(
                itemId,
                LocalDateTime.now(),
                BookingStatus.APPROVED);
    }

    @Override
    public List<Booking> getBookingsByUserId(BookingState state, long userId) {
        userService.getUserById(userId);
        LocalDateTime currentTime = LocalDateTime.now();
        List<Booking> bookings;
        switch (state) {
            case ALL:
                bookings = bookingRepository.findAllByBookerIdOrderByStartDesc(userId);
                break;
            case CURRENT:
                bookings = bookingRepository.findAllByBookerIdAndStartIsBeforeAndEndIsAfter(
                        userId,
                        currentTime,
                        currentTime);
                break;
            case PAST:
                bookings = bookingRepository.findAllByBookerIdAndEndIsBeforeOrderByStartDesc(
                        userId,
                        currentTime);
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByBookerIdAndStartIsAfterOrderByStartDesc(
                        userId,
                        LocalDateTime.now());
                break;
            case WAITING:
                bookings = bookingRepository.findAllByBookerIdAndStatus(
                        userId,
                        BookingStatus.WAITING);
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByBookerIdAndStatus(
                        userId,
                        BookingStatus.REJECTED);
                break;
            default:
                throw new BadMethodArgumentsException("User id = " + userId + " or state = " + state + "is wrong");
        }
        return bookings;
    }

    @Override
    public List<Booking> getOwnerItemsBookings(BookingState state, long userId) {
        userService.getUserById(userId);
        List<Booking> bookings;
        LocalDateTime currentTime = LocalDateTime.now();
        switch (state) {
            case ALL:
                bookings = bookingRepository.findAllByItemOwnerIdOrderByStartDesc(userId);
                break;
            case CURRENT:
                bookings = bookingRepository.findAllByItemOwnerIdAndStartIsBeforeAndEndIsAfter(
                        userId,
                        currentTime,
                        currentTime);
                break;
            case PAST:
                bookings = bookingRepository.findAllByItemOwnerIdAndEndIsBeforeOrderByStartDesc(
                        userId,
                        currentTime);
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByItemOwnerIdAndStartIsAfterOrderByStartDesc(
                        userId,
                        LocalDateTime.now());
                break;
            case WAITING:
                bookings = bookingRepository.findAllByItemOwnerIdAndStatus(
                        userId,
                        BookingStatus.WAITING);
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByItemOwnerIdAndStatus(
                        userId,
                        BookingStatus.REJECTED);
                break;
            default:
                throw new BadMethodArgumentsException("User id = " + userId + " or state = " + state + "is wrong");
        }
        return bookings;
    }

    @Override
    public List<Booking> getOwnerItemsSortedById(long userId) {
        if (userService.getUserById(userId) == null) {
            throw new BadUserException("There is no user with id = " + userId);
        }
        return bookingRepository.findAllByItemOwnerId(userId);
    }

    @Override
    public boolean isAllowedToComment(long userId, long itemId) {
        return bookingRepository.existsBookingByBookerIdAndItemIdAndStatusAndEndIsBefore(
                userId,
                itemId,
                BookingStatus.APPROVED,
                LocalDateTime.now());
    }
}
