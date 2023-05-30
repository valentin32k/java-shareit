package ru.practicum.shareit.request.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.List;

@Value
@Builder
public class OutputItemRequestDto {
    long id;
    String description;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    LocalDateTime created;
    List<ShortItemDto> items;

    @Value
    @Builder
    public static class ShortItemDto {
        long id;
        String name;
        String description;
        Boolean available;
        long requestId;
    }
}
