package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.exceptions.BadMethodArgumentsException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.UserRepository;

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
    private final UserRepository userRepository;
    private final ItemRequestRepository itemRequestRepository;

    @Override
    @Transactional
    public Item createItem(Item item, long ownerId, long requestId) {
        item.setOwner(userRepository
                .findById(ownerId)
                .orElseThrow(() -> new NotFoundException("User with id = " + ownerId + " is not found")));
        item.setRequest(itemRequestRepository
                .findById(requestId)
                .orElse(null));
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
            item.setLastBooking(
                    bookingRepository.findFirstByItemIdAndStartLessThanEqualAndStatusOrderByStartDesc(
                            itemId,
                            currentDate,
                            BookingStatus.APPROVED));
            item.setNextBooking(
                    bookingRepository.findFirstByItemIdAndStartIsAfterAndStatusOrderByStartAsc(
                            itemId,
                            currentDate,
                            BookingStatus.APPROVED));
        }
        return item;
    }

    @Override
    public List<Item> getUserItems(long ownerId, int from, int size) {
        List<Item> items = itemRepository.findAllByOwnerId(
                        ownerId,
                        PageRequest.of(from / size, size, Sort.by(ASC, "id")))
                .getContent();
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
                item.setNextBooking(
                        itemBookings.stream()
                                .filter(b -> b.getStart().isAfter(currentDate))
                                .findFirst()
                                .orElse(null));
                item.setLastBooking(
                        itemBookings.stream()
                                .filter(b -> !b.getStart().isAfter(currentDate))
                                .reduce((x, y) -> (x.getStart().isAfter(y.getStart())) ? x : y)
                                .orElse(null));
            }
        }
        return items;
    }

    @Override
    public List<Item> findItemsWithText(String text, int from, int size) {
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        return itemRepository
                .search(text, PageRequest.of(from / size, size, Sort.by(ASC, "id")))
                .getContent();
    }

    @Override
    @Transactional
    public Comment createComment(Comment comment, long itemId, long userId) {
        if (!isAllowedToComment(userId, itemId)) {
            throw new BadMethodArgumentsException("Comment not added");
        }
        comment.setItem(getItemById(itemId, userId));
        comment.setAuthor(userRepository
                .findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id = " + userId + " is not found")));
        comment.setCreated(LocalDateTime.now());
        return commentRepository.save(comment);
    }

    @Override
    public List<Comment> getItemComments(long itemId) {
        return commentRepository.findAllByItemId(itemId);
    }

    private boolean isAllowedToComment(long userId, long itemId) {
        return bookingRepository.existsBookingByBookerIdAndItemIdAndStatusAndEndIsBefore(
                userId,
                itemId,
                BookingStatus.APPROVED,
                LocalDateTime.now());
    }
}