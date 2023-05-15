package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Value;
import ru.practicum.shareit.booking.dto.OutputBookingDto;

import java.util.List;

@Value
@Builder
public class OutputItemDto {
    long id;
    String name;
    String description;
    Boolean available;
    OutputBookingDto nextBooking;
    OutputBookingDto lastBooking;
    List<OutputCommentDto> comments;

    public static class ShortItemDto {
        public long id;
        public String name;
    }
}
