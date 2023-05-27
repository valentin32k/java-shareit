package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InputCommentDto {
    private long id;
    @NotBlank(message = "Comment text can not be blank")
    @Size(max = 4096, message = "Comment text must be shorter than 4096 characters")
    private String text;
}
