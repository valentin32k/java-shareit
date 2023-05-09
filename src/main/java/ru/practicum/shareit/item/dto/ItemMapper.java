package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dto.BookingShort;
import ru.practicum.shareit.item.Comment;
import ru.practicum.shareit.item.Item;

import java.util.Collection;
import java.util.List;

public class ItemMapper {

    public static Item fromItemDto(ItemDto itemDto) {
        return Item.builder()
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .build();
    }

    public static Item fromItemBookingsDto(ItemBookingsDto itemBookingsDto) {
        return Item.builder()
                .id(itemBookingsDto.getId())
                .name(itemBookingsDto.getName())
                .description(itemBookingsDto.getDescription())
                .available(itemBookingsDto.getAvailable())
                .owner(itemBookingsDto.getOwner())
                .request(itemBookingsDto.getRequest())
                .build();
    }

    public static ItemBookingsDto toItemBookingsDto(Item item, BookingShort lastBooking, BookingShort nextBooking, List<Comment> comments) {
        return ItemBookingsDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .owner(item.getOwner())
                .request(item.getRequest())
                .lastBooking(lastBooking)
                .nextBooking(nextBooking)
                .comments(comments)
                .build();
    }
}
