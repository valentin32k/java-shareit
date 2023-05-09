package ru.practicum.shareit.item;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class InMemoryItemStorage implements ItemStorage {
    private final HashMap<Long, Item> itemsById = new HashMap<>();
    private long id = 0;

    @Override
    public Item createItem(Item item) {
        item.setId(++id);
        itemsById.put(id, item);
        return item;
    }

    @Override
    public Item updateItem(Item item) {
        itemsById.put(item.getId(), item);
        return item;
    }

    @Override
    public Item getItemById(Long itemId) {
        return itemsById.get(itemId);
    }

    @Override
    public List<Item> getUserItems(long ownerId) {
        return itemsById.values()
                .stream()
                .filter(i -> i.getOwner().getId() == ownerId)
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> findItemsWithText(String text) {
        if ("".equals(text)) {
            return new ArrayList<>();
        }
        return itemsById.values()
                .stream()
                .filter(i -> (i.getName().toLowerCase().contains(text.toLowerCase()) ||
                        i.getDescription().toLowerCase().contains(text.toLowerCase())) &&
                        i.getAvailable())
                .collect(Collectors.toList());
    }
}