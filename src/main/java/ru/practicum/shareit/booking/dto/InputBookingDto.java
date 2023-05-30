package ru.practicum.shareit.booking.dto;

import lombok.Data;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
public class InputBookingDto {
    @NotNull(message = "The field start date can not be null")
    @FutureOrPresent(message = "start booking time can not be on the past")
    private LocalDateTime start;
    @NotNull(message = "The field end date can not be null")
    @Future(message = "end booking time can not be on the past")
    private LocalDateTime end;
    private long itemId;
}
