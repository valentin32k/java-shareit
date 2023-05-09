package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Value;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Value
@Builder
public class CommentDto {

    @NotBlank(message = "Comment text can not be blank")
    @Size(max = 30000, message = "Comment text must be shorter than 30000 characters")
    String text;
}
