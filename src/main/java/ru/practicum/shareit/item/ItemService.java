package ru.practicum.shareit.item;

import java.util.List;

public interface ItemService {

    /**
     * Creates a new item
     *
     * @param item, ownerId
     * @return new item
     */
    Item createItem(Item item, long ownerId, long requestId);

    /**
     * Updates the item
     * If the current user is not the owner of the item throws NotFoundException
     *
     * @param item, itemId, ownerId
     * @return updated item
     */
    Item updateItem(Item item, long itemId, long ownerId);

    /**
     * Returns item by id
     * If the item is not found throws NotFoundException
     *
     * @param itemId, userId
     * @return item by id
     */
    Item getItemById(Long itemId, long userId);

    /**
     * Returns all user items
     *
     * @param ownerId
     * @return List of items
     */
    List<Item> getUserItems(long ownerId, int from, int size);

    /**
     * Find items with text
     * If the text is blank return empty ArrayList
     *
     * @param text
     * @return List of items
     */
    List<Item> findItemsWithText(String text, int from, int size);

    /**
     * Creates a new comment
     *
     * @param comment, itemId, userId
     * @return new comment
     */
    Comment createComment(Comment comment, long itemId, long userId);

    /**
     * Returns item comments by itemId
     *
     * @param itemId
     * @return List of comments
     */
    List<Comment> getItemComments(long itemId);
}
