package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.Item;

import java.time.LocalDateTime;

public interface BookingShort {
    long getId();

    LocalDateTime getStart();

    LocalDateTime getEnd();

    Item getItem();

    long getBookerId();

    BookingStatus getStatus();

}
