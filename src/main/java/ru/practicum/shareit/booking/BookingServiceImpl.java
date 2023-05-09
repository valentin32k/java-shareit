package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.exceptions.BadMethodArgumentsException;
import ru.practicum.shareit.exceptions.BadUserException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final ItemService itemService;

    @Override
    public Booking createBooking(BookingDto bookingDto, long bookerId) {
        Booking booking = BookingMapper.fromBookindDto(bookingDto);
        booking.setBooker(userService.getUserById(bookerId));
        Item item = ItemMapper.fromItemBookingsDto(itemService.getItemById(bookingDto.getItemId(),bookerId));
        if (item.getOwner().getId() == bookerId) {
            throw new NotFoundException("The user with id = " + bookerId + " is trying to book their item");
        }
        booking.setItem(item);
        if (!booking.getItem().getAvailable() ||
                !booking.getEnd().isAfter(booking.getStart())) {
            throw new BadMethodArgumentsException("Try to booking not available item with id = " + booking.getItem().getId());
        }
        return bookingRepository.save(booking);
    }

    @Override
    public Booking confirmBooking(long bookingId, long ownerId, Boolean approved) {
        Optional<Booking> booking = bookingRepository.findById(bookingId);
        if (booking.isEmpty()) {
            throw new NotFoundException("Can not find booking with id = " + bookingId);
        }
        Booking returnedBooking = booking.get();
        if (ownerId != returnedBooking.getItem().getOwner().getId() && approved != null) {
            if (ownerId == returnedBooking.getBooker().getId()) {
                throw new NotFoundException("Booker with id = " + ownerId + " try to confirm booking");
            } else {
                throw new BadMethodArgumentsException("Not item owner try to confirm booking");
            }
        }
        if (BookingStatus.APPROVED == returnedBooking.getStatus()  ||
                BookingStatus.REJECTED == returnedBooking.getStatus()) {
            throw new BadMethodArgumentsException("You can not change booking status");
        }
        if (approved) {
            returnedBooking.setStatus(BookingStatus.APPROVED);
        } else {
            returnedBooking.setStatus(BookingStatus.REJECTED);
        }
        return bookingRepository.save(returnedBooking);
    }

    @Transactional(readOnly = true)
    @Override
    public Booking getBookingById(long bookingId, long askUserId) {
        Optional<Booking> booking = bookingRepository.findById(bookingId);
        if (booking.isEmpty()) {
            throw new NotFoundException("Can not find booking with id = " + bookingId);
        }
        Booking returnedBooking = booking.get();
        if (askUserId != returnedBooking.getBooker().getId() &&
                askUserId != returnedBooking.getItem().getOwner().getId()) {
            throw new NotFoundException("Not item owner and not booker try to get booking");
        }
        return returnedBooking;
    }

    @Transactional(readOnly = true)
    @Override
    public List<Booking> getBookingsByUserId(BookingState state, long userId) {
        if (userService.getUserById(userId) == null) {
            throw new BadUserException("There is no user with id = ");
        }
        List<Booking> bookings;
        switch (state) {
            case ALL:
                bookings = bookingRepository.findAllByBookerIdOrderByStartDesc(userId);
                break;
            case CURRENT:
                bookings = null;
                break;
            case PAST:
                bookings = null;
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByBookerIdAndStartIsAfterOrderByStartDesc(userId, LocalDateTime.now());
                break;
            case WAITING:
                bookings = bookingRepository.findAllByBookerIdAndStatus(userId, BookingStatus.WAITING);
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByBookerIdAndStatus(userId, BookingStatus.REJECTED);
                break;
            default:
                throw new BadMethodArgumentsException("User id = " + userId + " or state = " + state + "is wrong");
        }
        return bookings;
    }

    @Override
    public List<Booking> getOwnerItemsBookings(BookingState state, long userId) {
        if (userService.getUserById(userId) == null) {
            throw new BadUserException("There is no user with id = ");
        }
        List<Booking> bookings;
        switch (state) {
            case ALL:
                bookings = bookingRepository.findAllByItemOwnerIdOrderByStartDesc(userId);
                break;
            case CURRENT:
                bookings = null;
                break;
            case PAST:
                bookings = null;
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByItemOwnerIdAndStartIsAfterOrderByStartDesc(userId, LocalDateTime.now());
                break;
            case WAITING:
                bookings = bookingRepository.findAllByItemOwnerIdAndStatus(userId, BookingStatus.WAITING);
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByItemOwnerIdAndStatus(userId, BookingStatus.REJECTED);
                break;
            default:
                throw new BadMethodArgumentsException("User id = " + userId + " or state = " + state + "is wrong");
        }
        return bookings;
    }
}
