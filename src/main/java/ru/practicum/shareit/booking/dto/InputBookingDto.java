package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InputBookingDto {
    @NotNull(message = "The field start date can not be null")
    @FutureOrPresent(message = "start booking time can not be on the past")
    private LocalDateTime start;
    @NotNull(message = "The field end date can not be null")
    @Future(message = "end booking time can not be on the past")
    private LocalDateTime end;
    private long itemId;
}
