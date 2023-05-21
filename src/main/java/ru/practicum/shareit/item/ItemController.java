package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.item.dto.CommentMapper;
import ru.practicum.shareit.item.dto.InputCommentDto;
import ru.practicum.shareit.item.dto.InputItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.dto.OutputCommentDto;
import ru.practicum.shareit.item.dto.OutputItemDto;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public OutputItemDto createItem(@RequestBody @Valid InputItemDto inputItemDto,
                                    @RequestHeader("X-Sharer-User-Id") long ownerId) {
        log.info("Request received POST /items: '{}' for user with ownerId = {}", inputItemDto, ownerId);
        return ItemMapper.toOutputItemDto(
                itemService.createItem(
                        ItemMapper.fromInputItemDto(inputItemDto),
                        ownerId, inputItemDto.getRequestId()));
    }

    @PatchMapping("/{itemId}")
    public OutputItemDto updateItem(@RequestBody InputItemDto inputItemDto,
                                    @PathVariable long itemId,
                                    @RequestHeader("X-Sharer-User-Id") long ownerId) {
        log.info("Request received PATCH /items: with id = {}", itemId);
        return ItemMapper.toOutputItemDto(
                itemService
                        .updateItem(
                                ItemMapper.fromInputItemDto(inputItemDto),
                                itemId,
                                ownerId));
    }

    @GetMapping("/{itemId}")
    public OutputItemDto getItemById(@PathVariable long itemId,
                                     @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Request received GET /items: with id = {}", itemId);
        return ItemMapper.toOutputItemDto(
                itemService.getItemById(
                        itemId,
                        userId));
    }

    @GetMapping
    public List<OutputItemDto> getUserItems(@RequestHeader("X-Sharer-User-Id") long ownerId,
                                            @RequestParam(defaultValue = "0") int from,
                                            @RequestParam(defaultValue = "20") int size) {
        log.info("Request received GET /items: with ownerId = {}", ownerId);
        return ItemMapper.toItemDtoList(itemService.getUserItems(ownerId, from, size));
    }

    @GetMapping("/search")
    public List<OutputItemDto> findItemsWithText(@RequestParam("text") String text,
                                                 @RequestParam(defaultValue = "0") int from,
                                                 @RequestParam(defaultValue = "20") int size) {
        log.info("Request received GET /items/search: with text = {}", text);
        return ItemMapper.toItemDtoList(itemService.findItemsWithText(text, from, size));
    }

    @PostMapping("/{itemId}/comment")
    public OutputCommentDto createComment(@RequestBody @Valid InputCommentDto commentDto,
                                          @RequestHeader("X-Sharer-User-Id") long userId,
                                          @PathVariable long itemId) {
        log.info("Request received POST /items/{itemId}/comment: " +
                "with text = {}, " +
                "itemId = {} " +
                "and userId = {}", commentDto.getText(), itemId, userId);
        return CommentMapper.toOutputCommentDto(
                itemService.createComment(
                        CommentMapper.fromInputCommentDto(commentDto),
                        itemId,
                        userId));
    }
}