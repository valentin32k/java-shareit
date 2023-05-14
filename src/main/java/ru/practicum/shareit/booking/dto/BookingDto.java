package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Value;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Value
@Builder
public class BookingDto {
    long id;
    @NotNull(message = "The field start date can not be null")
    @FutureOrPresent(message = "start booking time can not be on the past")
    LocalDateTime start;
    @NotNull(message = "The field end date can not be null")
    @FutureOrPresent(message = "end booking time can not be on the past")
    LocalDateTime end;
    @NotNull(message = "The field item can not be null")
    long itemId;
    long bookerId;
    Item item;
    User booker;
    BookingStatus status;
}
