package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
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
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        private LocalDateTime start;
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        private LocalDateTime end;
        private Long bookerId;
    }
}
