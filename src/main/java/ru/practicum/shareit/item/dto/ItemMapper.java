package ru.practicum.shareit.item.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.dto.BookingMapper;
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
        return OutputItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .lastBooking(BookingMapper.toOutputBookingDto(item.getLastBooking()))
                .nextBooking(BookingMapper.toOutputBookingDto(item.getNextBooking()))
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
