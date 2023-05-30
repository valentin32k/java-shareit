package ru.practicum.shareit.item.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class InputCommentDto {
    private long id;
    @NotBlank(message = "Comment text can not be blank")
    @Size(max = 4096, message = "Comment text must be shorter than 4096 characters")
    private String text;
}
