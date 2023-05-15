package ru.practicum.shareit.item.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.Comment;

import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class CommentMapper {

    public Comment fromInputCommentDto(InputCommentDto inputCommentDto) {
        return Comment.builder()
                .text(inputCommentDto.getText())
                .build();
    }

    public OutputCommentDto toOutputCommentDto(Comment comment) {
        if (comment == null) {
            return null;
        }
        return OutputCommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .authorName(comment.getAuthor().getName())
                .created(comment.getCreated())
                .build();
    }

    public List<OutputCommentDto> toOutputCommentDtoList(List<Comment> comments) {
        if (comments == null) {
            return null;
        }
        return comments.stream()
                .map(CommentMapper::toOutputCommentDto)
                .collect(Collectors.toList());
    }
}
