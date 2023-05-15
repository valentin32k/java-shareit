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
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.InputBookingDto;
import ru.practicum.shareit.booking.dto.OutputBookingDto;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.user.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {
    private final ItemService itemService;
    private final UserService userService;
    private final BookingService bookingService;

    @PostMapping
    public OutputBookingDto createBooking(@RequestBody @Valid InputBookingDto inputBookingDto,
                                          @RequestHeader("X-Sharer-User-Id") long bookerId) {
        log.info("Request receive POST /bookings: '{}' for booker with id = {}", inputBookingDto, bookerId);
        return BookingMapper.toOutputBookingDto(bookingService.createBooking(
                BookingMapper.fromInputBookingDto(inputBookingDto,
                        itemService.getItemById(inputBookingDto.getItemId(), bookerId),
                        userService.getUserById(bookerId))));
    }

    @PatchMapping("/{bookingId}")
    public OutputBookingDto confirmBooking(@PathVariable long bookingId,
                                           @RequestHeader("X-Sharer-User-Id") long ownerId,
                                           @RequestParam(value = "approved") Boolean approved) {
        log.info("Request receive PATCH /bookings for booking with id = {} " +
                "from user with id = {} " +
                "and approved = {}", bookingId, ownerId, approved);
        return BookingMapper.toOutputBookingDto(bookingService.confirmBooking(bookingId, ownerId, approved));
    }

    @GetMapping("/{bookingId}")
    public OutputBookingDto getBookingById(@PathVariable long bookingId,
                                           @RequestHeader("X-Sharer-User-Id") long askUserId) {
        log.info("Request receive GET /bookings for booking with id = {} " +
                "from user with id = {}", bookingId, askUserId);
        return BookingMapper.toOutputBookingDto(bookingService.getBookingById(bookingId, askUserId));
    }

    @GetMapping
    public List<OutputBookingDto> getBookingsByUserId(@RequestParam(value = "state", defaultValue = "ALL") BookingState state,
                                                      @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Request receive GET /bookings for user with id = {} " +
                "and state = {}", userId, state);
        return BookingMapper.toOutputBookingDtoList(
                bookingService.getBookingsByUserId(state, userId));
    }

    @GetMapping("/owner")
    public List<OutputBookingDto> getOwnerItemsBookings(@RequestParam(value = "state", defaultValue = "ALL") BookingState state,
                                                        @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Request receive GET /bookings/owner for user with id = {} " +
                "and state = {}", userId, state);
        return BookingMapper.toOutputBookingDtoList(
                bookingService.getOwnerItemsBookings(state, userId));
    }
}
