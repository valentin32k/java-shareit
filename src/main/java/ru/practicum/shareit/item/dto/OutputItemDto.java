package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.List;

@Value
@Builder
public class OutputItemDto {
    long id;
    String name;
    String description;
    Boolean available;
    ShortBookingDto nextBooking;
    ShortBookingDto lastBooking;
    List<OutputCommentDto> comments;
    long requestId;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ShortBookingDto {
        private long id;
        private LocalDateTime start;
        private LocalDateTime end;
        private Long bookerId;
    }
}
