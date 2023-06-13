package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> createItem(@RequestBody @Valid ItemDto itemDto,
                                             @RequestHeader("X-Sharer-User-Id") long ownerId) {
        log.info("Request received POST /items: '{}' for user with ownerId = {}", itemDto, ownerId);
        return itemClient.createItem(itemDto, ownerId);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestBody ItemDto itemDto,
                                             @PathVariable long itemId,
                                             @RequestHeader("X-Sharer-User-Id") long ownerId) {
        log.info("Request received PATCH /items: with id = {}", itemId);
        return itemClient.updateItem(itemDto, itemId, ownerId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@PathVariable long itemId,
                                              @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Request received GET /items: with id = {}", itemId);
        return itemClient.getItemById(itemId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getUserItems(@RequestHeader("X-Sharer-User-Id") long ownerId,
                                               @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                               @Positive @RequestParam(defaultValue = "20") int size) {
        log.info("Request received GET /items: with ownerId = {}", ownerId);
        return itemClient.getUserItems(ownerId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> findItemsWithText(@RequestParam("text") String text,
                                                    @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                                    @Positive @RequestParam(defaultValue = "20") int size) {
        log.info("Request received GET /items/search: with text = {}", text);
        return itemClient.findItemsWithText(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@RequestBody @Valid CommentDto commentDto,
                                                @RequestHeader("X-Sharer-User-Id") long userId,
                                                @PathVariable long itemId) {
        log.info("Request received POST /items/{itemId}/comment: " +
                "with text = {}, " +
                "itemId = {} " +
                "and userId = {}", commentDto.getText(), itemId, userId);
        return itemClient.createComment(
                commentDto,
                itemId,
                userId);
    }
}
