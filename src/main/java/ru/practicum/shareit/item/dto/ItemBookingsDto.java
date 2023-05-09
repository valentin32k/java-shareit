package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Value;
import lombok.With;
import ru.practicum.shareit.booking.dto.BookingShort;
import ru.practicum.shareit.item.Comment;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import java.util.List;

@Value
@Builder
public class ItemBookingsDto {

    long id;
    String name;
    String description;
    Boolean available;
    User owner;
    ItemRequest request;
    @With
    BookingShort lastBooking;
    @With
    BookingShort nextBooking;
    List<Comment> comments;
}
