package ru.practicum.shareit.booking.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class BookingMapper {

    public Booking fromInputBookingDto(InputBookingDto inputBookingDto, Item item, User booker) {
        return Booking.builder()
                .start(inputBookingDto.getStart())
                .end(inputBookingDto.getEnd())
                .item(item)
                .booker(booker)
                .status(BookingStatus.WAITING)
                .build();
    }

    public OutputBookingDto toOutputBookingDto(Booking booking) {
        if (booking == null) {
            return null;
        }
        OutputBookingDto.ShortItemDto returnedItem = new OutputBookingDto.ShortItemDto();
        returnedItem.id = booking.getItem().getId();
        returnedItem.name = booking.getItem().getName();
        OutputBookingDto.ShortUserDto returnedBooker = new OutputBookingDto.ShortUserDto();
        returnedBooker.id = booking.getBooker().getId();
        return OutputBookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(returnedItem)
                .booker(returnedBooker)
                .bookerId(returnedBooker.id)
                .status(booking.getStatus())
                .build();
    }

    public List<OutputBookingDto> toOutputBookingDtoList(List<Booking> bookings) {
        if (bookings == null) {
            return null;
        }
        return bookings.stream()
                .map(BookingMapper::toOutputBookingDto)
                .collect(Collectors.toList());
    }
}
