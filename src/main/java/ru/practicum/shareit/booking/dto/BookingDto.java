package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Value;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Value
@Builder
public class BookingDto {

    @NotNull(message = "Item id cannot be empty")
    long itemId;

    @NotNull
    @FutureOrPresent(message = "start booking time can not be on the past")
    LocalDateTime start;

    @NotNull
    @FutureOrPresent(message = "end booking time can not be on the past")
    LocalDateTime end;
}
