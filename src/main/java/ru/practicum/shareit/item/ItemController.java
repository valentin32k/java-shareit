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
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.exceptions.BadMethodArgumentsException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {
    private final ItemService itemService;
    private final UserService userService;
    private final BookingService bookingService;

    @PostMapping
    public ItemDto createItem(@RequestBody @Valid ItemDto itemDto,
                              @RequestHeader("X-Sharer-User-Id") long ownerId) {
        log.info("Request received POST /items: '{}' for user with ownerId = {}", itemDto, ownerId);
        User user = userService.getUserById(ownerId);
        Item item = ItemMapper.fromItemDto(itemDto);
        item.setOwner(user);
        return ItemMapper.toItemDto(itemService.createItem(item), null, null, null);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestBody ItemDto itemDto,
                              @PathVariable long itemId,
                              @RequestHeader("X-Sharer-User-Id") long ownerId) {
        log.info("Request received PATCH /items: with id = {}", itemId);
        Item newItem = ItemMapper.fromItemDto(itemDto);
        newItem.setOwner(userService.getUserById(ownerId));
        newItem.setId(itemId);
        List<CommentDto> comments = itemService.getItemComments(itemId)
                .stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
        return ItemMapper.toItemDto(itemService.updateItem(newItem), null, null, comments);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@PathVariable long itemId,
                               @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Request received GET /items: with id = {}", itemId);
        Item item = itemService.getItemById(itemId);
        List<CommentDto> comments = itemService.getItemComments(itemId)
                .stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
        BookingDto lastBooking = null;
        BookingDto nextBooking = null;
        if (item.getOwner().getId() == userId) {
            lastBooking = BookingMapper.toBookingDto(bookingService.getLastBooking(itemId));
            nextBooking = BookingMapper.toBookingDto(bookingService.getNextBooking(itemId));
        }
        return ItemMapper.toItemDto(item, lastBooking, nextBooking, comments);
    }

    @GetMapping
    public List<ItemDto> getUserItems(@RequestHeader("X-Sharer-User-Id") long ownerId) {
        log.info("Request received GET /items: with ownerId = {}", ownerId);
        List<Item> items = itemService.getUserItems(ownerId);
        List<BookingDto> bookings = bookingService.getOwnerItemsSortedById(ownerId)
                .stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
        Map<Long, List<BookingDto>> bookingsByItemIds = new HashMap<>();
        List<ItemDto> returnedItems = new ArrayList<>();
        LocalDateTime currentTime = LocalDateTime.now();
        for (BookingDto booking : bookings) {
            List<BookingDto> itemBookingsList = bookingsByItemIds.get(booking.getItem().getId());
            if (itemBookingsList == null) {
                itemBookingsList = new ArrayList<>();
            }
            itemBookingsList.add(booking);
            bookingsByItemIds.put(booking.getItem().getId(), itemBookingsList);
        }
        for (Item item : items) {
            List<BookingDto> itemBookings = bookingsByItemIds.get(item.getId());
            if (itemBookings == null) {
                ItemDto itemDto = ItemMapper.toItemDto(item, null, null, null);
                returnedItems.add(itemDto);
            } else {
                BookingDto last = itemBookings.get(0);
                BookingDto next = null;
                for (BookingDto itemBooking : itemBookings) {
                    if (itemBooking.getStart().isAfter(currentTime)) {
                        if (next == null ||
                                itemBooking.getStart().isBefore(next.getStart())) {
                            next = itemBooking;
                        }
                    }
                }
                ItemDto itemDto = ItemMapper.toItemDto(item, last, next, null);
                returnedItems.add(itemDto);
            }
        }
        return returnedItems;
    }

    @GetMapping("/search")
    public List<ItemDto> findItemsWithText(@RequestParam("text") String text) {
        log.info("Request received GET /items/search: with text = {}", text);
        return itemService.findItemsWithText(text)
                .stream()
                .map(i -> ItemMapper.toItemDto(i, null, null, null))
                .collect(Collectors.toList());
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@RequestBody @Valid CommentDto commentDto,
                                    @RequestHeader("X-Sharer-User-Id") long userId,
                                    @PathVariable long itemId) {
        if (!bookingService.isAllowedToComment(userId, itemId)) {
            throw new BadMethodArgumentsException("Comment not added");
        }
        return CommentMapper.toCommentDto(itemService
                .createComment(CommentMapper.fromCommentDto(commentDto,
                        itemService.getItemById(itemId),
                        userService.getUserById(userId),
                        LocalDateTime.now())));
    }
}