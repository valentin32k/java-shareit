package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Value;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.dto.UserDto;

import javax.persistence.Column;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Value
@Builder
public class ItemDto {
    long id;
    @NotEmpty(message = "The field name can not be empty")
    @Size(max = 255, message = "Name must be shorter than 255 characters")
    String name;
    @NotEmpty
    @Size(max = 1024, message = "Description must be shorter than 1024 characters")
    String description;
    @NotNull(message = "Description cannot be null")
    @Column(name = "is_available", nullable = false)
    Boolean available;
    UserDto owner;
    ItemRequest request;
    BookingDto nextBooking;
    BookingDto lastBooking;
    List<CommentDto> comments;
}
