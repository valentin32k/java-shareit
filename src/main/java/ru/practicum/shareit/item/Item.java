package ru.practicum.shareit.item;

import lombok.Builder;
import lombok.Value;
import lombok.With;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Value
@Builder
public class Item {

    @With long id;

    @NotBlank(message = "Name cannot be empty")
    @Size(max = 100, message = "Name must be shorter than 100 characters")
    @With String name;

    @NotNull(message = "Description cannot be null")
    @Size(max = 300, message = "Description must be shorter than 300 characters")
    @With String description;

    @NotNull(message = "Description cannot be null")
    @With Boolean available;

    @With User owner;

    ItemRequest request;
}
