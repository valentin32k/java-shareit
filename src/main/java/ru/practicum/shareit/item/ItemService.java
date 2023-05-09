package ru.practicum.shareit.item;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.CommentsShort;
import ru.practicum.shareit.item.dto.ItemBookingsDto;
import ru.practicum.shareit.user.User;

import java.util.List;

public interface ItemService {

    /**
     * Creates a new item
     *
     * @param item
     * @return new item
     */
    Item createItem(Item item);

    /**
     * Updates the item
     * If the current user is not the owner of the item throws NotFoundException
     *
     * @param item
     * @return updated item
     */
    Item updateItem(Item item);

    /**
     * Returns item by id
     * If the item is not found throws NotFoundException
     *
     * @param itemId
     * @return item by id
     */
    ItemBookingsDto getItemById(Long itemId, long userId);

    /**
     * Returns all items by ownerId
     *
     * @param ownerId
     * @return List of items
     */
    List<ItemBookingsDto> getUserItems(long ownerId);

    /**
     * Find items with text
     *
     * @param text
     * @return List of items
     */
    List<Item> findItemsWithText(String text);


    Comment createComment(String commentText, Item item, User author);
}
