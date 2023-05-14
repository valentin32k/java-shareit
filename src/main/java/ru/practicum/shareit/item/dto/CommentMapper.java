package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.item.Comment;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

public class CommentMapper {
    public static Comment fromCommentDto(CommentDto commentDto, Item item, User author, LocalDateTime created) {
        return Comment.builder()
                .text(commentDto.getText())
                .item(item)
                .author(author)
                .created(created)
                .build();
    }

    public static CommentDto toCommentDto(Comment comment) {
        if (comment == null) {
            return null;
        }
        return CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .authorName(comment.getAuthor().getName())
                .created(comment.getCreated())
                .build();
    }
}
