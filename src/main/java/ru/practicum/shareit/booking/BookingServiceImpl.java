package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.BadMethodArgumentsException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.data.domain.Sort.Direction.DESC;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;

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
    public List<Booking> getBookingsByUserId(BookingState state, long userId) {
        userRepository
                .findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id = " + userId + " is not found"));
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
        userRepository
                .findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id = " + userId + " is not found"));
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
                        BookingStatus.WAITING, Sort.by(DESC, "start"));
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByItemOwnerIdAndStatus(
                        userId,
                        BookingStatus.REJECTED,
                        Sort.by(DESC, "start"));
                break;
            default:
                throw new BadMethodArgumentsException("User id = " + userId + " or state = " + state + "is wrong");
        }
        return bookings;
    }
}
