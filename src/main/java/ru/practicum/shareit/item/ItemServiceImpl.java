package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingShort;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.ItemBookingsDto;
import ru.practicum.shareit.item.dto.ItemMapper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Transactional
    @Override
    public Item createItem(Item item) {
        return itemRepository.save(item);
    }

    @Transactional
    @Override
    public Item updateItem(Item item) {
        Item currentItem = ItemMapper.fromItemBookingsDto(getItemById(item.getId(), 0));
        if (item.getOwner().getId() != currentItem.getOwner().getId()) {
            throw new NotFoundException("Only the owner can update a post");
        }
        if (item.getName() != null) {
            currentItem.setName(item.getName());
        }
        if (item.getDescription() != null) {
            currentItem.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            currentItem.setAvailable(item.getAvailable());
        }
        return itemRepository.save(currentItem);
    }

    @Override
    public ItemBookingsDto getItemById(Long itemId, long userId) {
        Optional<Item> item = itemRepository.findById(itemId);
        if (item.isEmpty()) {
            throw new NotFoundException("Item with id = " + itemId + " is not found");
        }
        Item tmpItem = item.get();
        if (tmpItem.getOwner().getId() != userId) {
            return ItemMapper.toItemBookingsDto(tmpItem, null, null);
        }
        BookingShort lastBooking = bookingRepository.findFirstByItemIdAndStatusOrderByStartAsc(itemId, BookingStatus.APPROVED);
        BookingShort nextBooking = bookingRepository.findFirstByItemIdAndStartIsAfterAndStatusOrderByStartAsc(itemId, LocalDateTime.now(), BookingStatus.APPROVED);
        return ItemMapper.toItemBookingsDto(tmpItem, lastBooking, nextBooking);
    }

    @Override
    public List<ItemBookingsDto> getUserItems(long ownerId) {
        List<Item> items = itemRepository.findAllByOwnerId(ownerId);
        List<BookingShort> bookings = bookingRepository.findAllByItemOwnerId(ownerId);
        Map<Long, List<BookingShort>> bookingsByItemIds = new HashMap<>();
        List<ItemBookingsDto> returnedItems = new ArrayList<>();
        LocalDateTime currentTime = LocalDateTime.now();
        for (BookingShort booking : bookings) {
            List<BookingShort> itemBookingsList = bookingsByItemIds.get(booking.getItem().getId());
            if (itemBookingsList == null) {
                itemBookingsList = new ArrayList<>();
            }
            itemBookingsList.add(booking);
            bookingsByItemIds.put(booking.getItem().getId(), itemBookingsList);
        }
        for (Item item : items) {
            List<BookingShort> itemBookings = bookingsByItemIds.get(item.getId());
            if (itemBookings == null) {
                ItemBookingsDto itemDto = ItemMapper.toItemBookingsDto(item, null, null);
                returnedItems.add(itemDto);
            } else {
                BookingShort last = itemBookings.get(0);
                BookingShort next = null;
                for (BookingShort itemBooking : itemBookings) {
                    itemBooking.getStatus();
                    itemBooking.getStatus();
                    if (itemBooking.getStart().isAfter(currentTime)) {
                        if (next == null ||
                                itemBooking.getStart().isBefore(next.getStart())) {
                            next = itemBooking;
                        }
                    }
                }
                ItemBookingsDto itemDto = ItemMapper.toItemBookingsDto(item, last, next);
                returnedItems.add(itemDto);
            }
        }
        return returnedItems;
    }

    @Override
    public List<Item> findItemsWithText(String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        return itemRepository.search(text);
    }

    @Override
    public Comment createComment(Comment comment) {
        return commentRepository.save(comment);
    }
}