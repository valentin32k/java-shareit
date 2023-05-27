package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Value;
import ru.practicum.shareit.booking.BookingStatus;

import java.time.LocalDateTime;

@Value
@Builder
public class OutputBookingDto {
    long id;
    LocalDateTime start;
    LocalDateTime end;
    ShortItemDto item;
    ShortUserDto booker;
    long bookerId;
    BookingStatus status;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ShortItemDto {
        private long id;
        private String name;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ShortUserDto {
        private long id;
    }
}


