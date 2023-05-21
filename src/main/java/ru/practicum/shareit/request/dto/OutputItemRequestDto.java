package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.List;

@Value
@Builder
public class OutputItemRequestDto {
    long id;
    String description;
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
