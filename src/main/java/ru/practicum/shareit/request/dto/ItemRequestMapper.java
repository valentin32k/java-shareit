package ru.practicum.shareit.request.dto;

import lombok.experimental.UtilityClass;
import org.springframework.data.domain.Page;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.request.ItemRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class ItemRequestMapper {
    public ItemRequest fromInputItemRequestDto(InputItemRequestDto inputItemRequestDto) {
        return ItemRequest.builder()
                .description(inputItemRequestDto.getDescription())
                .build();
    }

    public OutputItemRequestDto toOutputItemRequestDto(ItemRequest itemRequest) {
        List<Item> items = itemRequest.getItems();
        List<OutputItemRequestDto.ShortItemDto> shortItems = new ArrayList<>();
        if (items != null) {
            items.forEach(i -> shortItems.add(OutputItemRequestDto.ShortItemDto.builder()
                    .id(i.getId())
                    .name(i.getName())
                    .description(i.getDescription())
                    .available(i.getAvailable())
                    .requestId(i.getRequest().getId())
                    .build()));
        }
        return OutputItemRequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .items(shortItems)
                .build();
    }

    public List<OutputItemRequestDto> toOutputItemRequestDtoList(List<ItemRequest> requests) {
        return requests.stream()
                .map(ItemRequestMapper::toOutputItemRequestDto)
                .collect(Collectors.toList());
    }
}
