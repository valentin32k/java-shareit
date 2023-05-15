package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.exceptions.BadMethodArgumentsException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final CommentRepository commentRepository;
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
        if (item.getName() != null) {
            if (!item.getName().isBlank()) {
                currentItem.setName(item.getName());
            }
        }
        if (item.getDescription() != null) {
            if (!item.getDescription().isBlank()) {
                currentItem.setDescription(item.getDescription());
            }
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
            item.setLastBooking(bookingService.getLastBooking(itemId));
            item.setNextBooking(bookingService.getNextBooking(itemId));
        }
        return item;
    }

    @Override
    public List<Item> getUserItems(long ownerId) {
        List<Item> items = itemRepository.findAllByOwnerId(ownerId);
        List<Booking> bookings = bookingService.getOwnerItemsSortedById(ownerId);
        Map<Long, List<Booking>> bookingsByItemIds = new HashMap<>();

        for (Booking booking : bookings) {
            List<Booking> itemBookingsList = bookingsByItemIds.get(booking.getItem().getId());
            if (itemBookingsList == null) {
                itemBookingsList = new ArrayList<>();
            }
            itemBookingsList.add(booking);
            bookingsByItemIds.put(booking.getItem().getId(), itemBookingsList);
        }
        for (Item item : items) {
            List<Booking> itemBookings = bookingsByItemIds.get(item.getId());
            if (itemBookings != null) {
                item.setLastBooking(itemBookings.get(0));
                for (Booking itemBooking : itemBookings) {
                    if (itemBooking.getStart().isAfter(LocalDateTime.now())) {
                        if (item.getNextBooking() == null ||
                                itemBooking.getStart().isBefore(item.getNextBooking().getStart())) {
                            item.setNextBooking(itemBooking);
                        }
                    }
                }
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