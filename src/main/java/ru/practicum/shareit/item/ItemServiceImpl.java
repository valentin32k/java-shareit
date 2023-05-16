package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.exceptions.BadMethodArgumentsException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static org.springframework.data.domain.Sort.Direction.ASC;
import static org.springframework.data.domain.Sort.Direction.DESC;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;
    private final BookingService bookingService;
    private final UserService userService;

    @Override
    @Transactional
    public Item createItem(Item item, long ownerId) {
        item.setOwner(userService.getUserById(ownerId));
        return itemRepository.save(item);
    }

    @Override
    @Transactional
    public Item updateItem(Item item, long itemId, long ownerId) {
        Item currentItem = getItemById(itemId, ownerId);
        if (ownerId != currentItem.getOwner().getId()) {
            throw new NotFoundException("Only the owner can update a post");
        }
        if (item.getName() != null && !item.getName().isBlank()) {
            currentItem.setName(item.getName());
        }
        if (item.getDescription() != null && !item.getDescription().isBlank()) {
            currentItem.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            currentItem.setAvailable(item.getAvailable());
        }
        currentItem.setComments(getItemComments(itemId));
        return currentItem;
    }

    @Override
    public Item getItemById(Long itemId, long userId) {
        Item item = itemRepository
                .findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item with id = " + itemId + " is not found"));
        item.setComments(getItemComments(itemId));
        if (item.getOwner().getId() == userId) {
            LocalDateTime currentDate = LocalDateTime.now();
            item.setLastBooking(bookingService.getLastBooking(itemId, currentDate));
            item.setNextBooking(bookingService.getNextBooking(itemId, currentDate));
        }
        return item;
    }

    @Override
    public List<Item> getUserItems(long ownerId) {
        List<Item> items = itemRepository.findAllByOwnerId(ownerId);
        Map<Item, List<Comment>> comments = commentRepository.findByItemIn(items, Sort.by(DESC, "created"))
                .stream()
                .collect(groupingBy(Comment::getItem, toList()));
        Map<Item, List<Booking>> bookings = bookingRepository.findByItemIn(items, Sort.by(ASC, "start"))
                .stream()
                .collect(groupingBy(Booking::getItem, toList()));
        LocalDateTime currentDate = LocalDateTime.now();
        for (Item item : items) {
            item.setComments(comments.get(item));
            List<Booking> itemBookings = bookings.get(item);
            if (itemBookings != null) {
                List<Booking> before = itemBookings.stream().filter(b -> b.getStart().isBefore(currentDate)).collect(toList());
                List<Booking> after = itemBookings.stream().filter(b -> b.getStart().isAfter(currentDate)).collect(toList());
                item.setNextBooking(after.get(0));
                item.setLastBooking(before.get(before.size() - 1));
            }
        }
        return items;
    }

    @Override
    public List<Item> findItemsWithText(String text) {
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        return itemRepository.search(text);
    }

    @Override
    @Transactional
    public Comment createComment(Comment comment, long itemId, long userId) {
        if (!bookingService.isAllowedToComment(userId, itemId)) {
            throw new BadMethodArgumentsException("Comment not added");
        }
        comment.setItem(getItemById(itemId, userId));
        comment.setAuthor(userService.getUserById(userId));
        comment.setCreated(LocalDateTime.now());
        return commentRepository.save(comment);
    }

    @Override
    public List<Comment> getItemComments(long itemId) {
        return commentRepository.findAllByItemId(itemId);
    }
}