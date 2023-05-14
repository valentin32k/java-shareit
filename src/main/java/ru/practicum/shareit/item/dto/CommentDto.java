package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Value;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Value
@Builder
public class CommentDto {
    long id;
    @NotEmpty(message = "Comment text can not be empty")
    @Size(max = 4096, message = "Comment text must be shorter than 4096 characters")
    String text;
    String authorName;
    LocalDateTime created;
}
