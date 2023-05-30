package ru.practicum.shareit.item.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class InputItemDto {
    @NotBlank(message = "The field name can not be blank")
    @Size(max = 255, message = "Name must be shorter than 255 characters")
    private String name;
    @NotBlank(message = "The field description can not be blank")
    @Size(max = 1024, message = "Description must be shorter than 1024 characters")
    private String description;
    @NotNull(message = "Available cannot be null")
    private Boolean available;
    private long requestId;
}
