package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> bookItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                           @RequestBody @Valid BookItemRequestDto requestDto) {
        log.info("Creating booking {}, userId={}", requestDto, userId);
        return bookingClient.bookItem(userId, requestDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> confirmBooking(@PathVariable long bookingId,
                                                 @RequestHeader("X-Sharer-User-Id") long ownerId,
                                                 @RequestParam(value = "approved") Boolean approved) {
        log.info("Request receive PATCH /bookings for booking with id = {} " +
                "from user with id = {} " +
                "and approved = {}", bookingId, ownerId, approved);
        return bookingClient.confirmBooking(bookingId, ownerId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBookingById(@PathVariable long bookingId,
                                                 @RequestHeader("X-Sharer-User-Id") long askUserId) {
        log.info("Request receive GET /bookings for booking with id = {} " +
                "from user with id = {}", bookingId, askUserId);
        return bookingClient.getBookingById(bookingId, askUserId);
    }

    @GetMapping
    public ResponseEntity<Object> getBookingsByUserId(@RequestParam(name = "state", defaultValue = "ALL") String stateParam,
                                                      @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                                      @Positive @RequestParam(defaultValue = "20") int size,
                                                      @RequestHeader("X-Sharer-User-Id") long userId) {
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
        log.info("Request receive GET /bookings for user with id = {} " +
                "and state = {}", userId, state);
        return bookingClient.getBookingsByUserId(state, from, size, userId);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getOwnerItemsBookings(@RequestParam(name = "state", defaultValue = "ALL") String stateParam,
                                                        @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                                        @Positive @RequestParam(defaultValue = "20") int size,
                                                        @RequestHeader("X-Sharer-User-Id") long userId) {
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
        log.info("Request receive GET /bookings/owner for user with id = {} " +
                "and state = {}", userId, state);
        return bookingClient.getOwnerItemsBookings(state, from, size, userId);
    }
}
