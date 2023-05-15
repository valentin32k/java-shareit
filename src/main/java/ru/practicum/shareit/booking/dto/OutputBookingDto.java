package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Value;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.dto.OutputItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

@Value
@Builder
public class OutputBookingDto {
    long id;
    LocalDateTime start;
    LocalDateTime end;
    OutputItemDto.ShortItemDto item;
    UserDto.ShortUserDto booker;
    long bookerId;
    BookingStatus status;
}
