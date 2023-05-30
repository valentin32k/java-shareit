package ru.practicum.shareit.request.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class InputItemRequestDto {

    @NotBlank(message = "The field description can not be blank")
    @Size(max = 1024, message = "Description must be shorter than 1024 characters")
    private String description;
}
