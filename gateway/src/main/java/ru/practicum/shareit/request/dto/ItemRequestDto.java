package ru.practicum.shareit.request.dto;

import lombok.Value;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Value
public class ItemRequestDto {
    long id;
    @NotBlank(message = "The field description can not be blank")
    @Size(max = 1024, message = "Description must be shorter than 1024 characters")
    String description;
}
