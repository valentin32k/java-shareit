package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Value;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Value
@Builder
public class InputBookingDto {
    @NotNull(message = "The field start date can not be null")
    @FutureOrPresent(message = "start booking time can not be on the past")
    LocalDateTime start;
    @NotNull(message = "The field end date can not be null")
    @Future(message = "end booking time can not be on the past")
    LocalDateTime end;
    long itemId;
}
