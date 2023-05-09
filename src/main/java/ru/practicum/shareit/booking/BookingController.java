package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.booking.dto.BookingDto;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public Booking createBooking(@RequestBody @Valid BookingDto bookingDto,
                                 @RequestHeader("X-Sharer-User-Id") long bookerId) {
        log.info("Request receive POST /bookings: '{}' for booker with id = {}", bookingDto, bookerId);
        return bookingService.createBooking(bookingDto, bookerId);
    }

    @PatchMapping("/{bookingId}")
    public Booking confirmBooking(@PathVariable long bookingId,
                                  @RequestHeader("X-Sharer-User-Id") long ownerId,
                                  @RequestParam(value = "approved") Boolean approved) {
        log.info("Request receive PATCH /bookings for booking with id = {} " +
                "from user with id = {} " +
                "and approved = {}", bookingId, ownerId, approved);
        return bookingService.confirmBooking(bookingId, ownerId, approved);
    }

    @GetMapping("/{bookingId}")
    public Booking getBookingById(@PathVariable long bookingId,
                                  @RequestHeader("X-Sharer-User-Id") long askUserId) {
        log.info("Request receive GET /bookings for booking with id = {} " +
                "from user with id = {}", bookingId, askUserId);
        return bookingService.getBookingById(bookingId, askUserId);
    }

    @GetMapping
    public List<Booking> getBookingsByUserId(@RequestParam(value = "state", defaultValue = "ALL", required = false) BookingState state,
                                             @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Request receive GET /bookings for user with id = {} " +
                "and state = {}", userId, state);
        return bookingService.getBookingsByUserId(state, userId);
    }

    @GetMapping("/owner")
    public List<Booking> getOwnerItemsBookings(@RequestParam(value = "state", defaultValue = "ALL", required = false) BookingState state,
                                              @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Request receive GET /bookings/owner for user with id = {} " +
                "and state = {}", userId, state);
        return bookingService.getOwnerItemsBookings(state, userId);
    }
}
