package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Value;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Value
@Builder
public class InputCommentDto {
    long id;
    @NotBlank(message = "Comment text can not be blank")
    @Size(max = 4096, message = "Comment text must be shorter than 4096 characters")
    String text;
}
