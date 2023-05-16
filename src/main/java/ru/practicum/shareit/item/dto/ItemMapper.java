package ru.practicum.shareit.item.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.item.Item;

import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class ItemMapper {
    public Item fromInputItemDto(InputItemDto inputItemDto) {
        return Item.builder()
                .name(inputItemDto.getName())
                .description(inputItemDto.getDescription())
                .available(inputItemDto.getAvailable())
                .build();
    }

    public OutputItemDto toOutputItemDto(Item item) {
        if (item == null) {
            return null;
        }
        Booking last = item.getLastBooking();
        Booking next = item.getNextBooking();
        OutputItemDto.ShortBookingDto lastBooking = null;
        OutputItemDto.ShortBookingDto nextBooking = null;
        if (last != null) {
            lastBooking = OutputItemDto.ShortBookingDto.builder()
                    .id(last.getId())
                    .start(last.getStart())
                    .end(last.getEnd())
                    .bookerId(last.getBooker().getId())
                    .build();
        }
        if (next != null) {
            nextBooking = OutputItemDto.ShortBookingDto.builder()
                    .id(next.getId())
                    .start(next.getStart())
                    .end(next.getEnd())
                    .bookerId(next.getBooker().getId())
                    .build();
        }
        return OutputItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .lastBooking(lastBooking)
                .nextBooking(nextBooking)
                .comments(CommentMapper.toOutputCommentDtoList(item.getComments()))
                .build();
    }

    public List<OutputItemDto> toItemDtoList(List<Item> items) {
        if (items == null) {
            return null;
        }
        return items.stream()
                .map(ItemMapper::toOutputItemDto)
                .collect(Collectors.toList());
    }
}
