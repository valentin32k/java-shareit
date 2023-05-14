package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.NotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final CommentRepository commentRepository;

    @Override
    @Transactional
    public Item createItem(Item item) {
        return itemRepository.save(item);
    }

    @Override
    @Transactional
    public Item updateItem(Item item) {
        Item currentItem = getItemById(item.getId());
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
    public Item getItemById(Long itemId) {
        Optional<Item> item = itemRepository.findById(itemId);
        if (item.isEmpty()) {
            throw new NotFoundException("Item with id = " + itemId + " is not found");
        }
        return item.get();
    }

    @Override
    public List<Item> getUserItems(long ownerId) {
        return itemRepository.findAllByOwnerId(ownerId);
    }

    @Override
    public List<Item> findItemsWithText(String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        return itemRepository.search(text);
    }

    @Override
    @Transactional
    public Comment createComment(Comment comment) {
        return commentRepository.save(comment);
    }

    @Override
    public List<Comment> getItemComments(long itemId) {
        return commentRepository.findAllByItemId(itemId);
    }
}