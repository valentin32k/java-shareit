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
import ru.practicum.shareit.item.dto.ItemBookingsDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {
    private final ItemService itemService;
    private final UserService userService;

    @PostMapping
    public Item createItem(@RequestBody @Valid ItemDto itemDto,
                           @RequestHeader("X-Sharer-User-Id") long ownerId) {
        log.info("Request received POST /items: '{}' for user with ownerId = {}", itemDto, ownerId);
        User user = userService.getUserById(ownerId);
        Item item = ItemMapper.fromItemDto(itemDto);
        item.setOwner(user);
        return itemService.createItem(item);
    }

    @PatchMapping("/{itemId}")
    public Item updateItem(@RequestBody ItemDto itemDto,
                           @PathVariable long itemId,
                           @RequestHeader("X-Sharer-User-Id") long ownerId) {
        log.info("Request received PATCH /items: with id = {}", itemId);
        Item newItem = ItemMapper.fromItemDto(itemDto);
        newItem.setOwner(userService.getUserById(ownerId));
        newItem.setId(itemId);
        return itemService.updateItem(newItem);
    }

    @GetMapping("/{itemId}")
    public ItemBookingsDto getItemById(@PathVariable long itemId,
                                       @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Request received GET /items: with id = {}", itemId);
        return itemService.getItemById(itemId, userId);
    }

    @GetMapping
    public List<ItemBookingsDto> getUserItems(@RequestHeader("X-Sharer-User-Id") long ownerId) {
        log.info("Request received GET /items: with ownerId = {}", ownerId);
        return itemService.getUserItems(ownerId);
    }

    @GetMapping("/search")
    public List<Item> findItemsWithText(@RequestParam("text") String text) {
        log.info("Request received GET /items/search: with text = {}", text);
        return itemService.findItemsWithText(text);
    }

    @PostMapping("/{itemId}/comments")
    public Comment createComment(@RequestBody @Valid Comment commentDto,
                                 @RequestHeader("X-Sharer-User-Id") long userId,
                                 @PathVariable long itemId) {
        Comment comment = Comment.builder()
                .text(commentDto.getText())
                .item(ItemMapper.fromItemBookingsDto(itemService.getItemById(itemId, userId)))
                .author(userService.getUserById(userId))
                .build();
        return itemService.createComment(comment);
    }
}
