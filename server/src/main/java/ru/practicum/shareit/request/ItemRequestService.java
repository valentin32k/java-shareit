package ru.practicum.shareit.request;

import java.util.List;

public interface ItemRequestService {
    /**
     * Creates a new itemRequest
     *
     * @param itemRequest, userId
     * @return new ItemRequesto
     */
    ItemRequest createItemRequest(ItemRequest itemRequest, long userId);

    /**
     * Returns all user item requests
     *
     * @param userId
     * @return List of requests
     */
    List<ItemRequest> getUserItemRequests(long userId);

    /**
     * Returns the specified number of item requests
     *
     * @param from, size, userId
     * @return List of requests
     */
    List<ItemRequest> getItemRequests(int from, int size, long userId);

    /**
     * Returns the item request by id
     *
     * @param requestId, userId
     * @return request
     */
    ItemRequest getItemRequestById(long requestId, long userId);
}
