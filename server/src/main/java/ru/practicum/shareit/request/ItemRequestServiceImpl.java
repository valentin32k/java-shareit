package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static org.springframework.data.domain.Sort.Direction.DESC;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final ItemRequestRepository itemRequestRepository;

    @Override
    @Transactional
    public ItemRequest createItemRequest(ItemRequest itemRequest, long userId) {
        itemRequest.setRequestor(userRepository
                .findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id = " + userId + " is not found")));
        itemRequest.setCreated(LocalDateTime.now());
        return itemRequestRepository.save(itemRequest);
    }

    @Override
    public List<ItemRequest> getUserItemRequests(long userId) {
        userRepository
                .findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id = " + userId + " is not found"));
        List<ItemRequest> itemRequests = itemRequestRepository
                .findAllByRequestorId(userId, Sort.by(DESC, "created"));
        addItemsToItemsRequests(itemRequests);
        return itemRequests;
    }

    @Override
    public List<ItemRequest> getItemRequests(int from, int size, long userId) {
        List<ItemRequest> itemRequests = itemRequestRepository
                .findAllByRequestorIdNot(userId, PageRequest.of(from / size, size, Sort.by(DESC, "created")))
                .getContent();
        addItemsToItemsRequests(itemRequests);
        return itemRequests;
    }

    @Override
    public ItemRequest getItemRequestById(long requestId, long userId) {
        userRepository
                .findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id = " + userId + " is not found"));
        ItemRequest itemRequest = itemRequestRepository
                .findById(requestId)
                .orElseThrow(() -> new NotFoundException("Item request with id = " + requestId + " is not found"));
        addItemsToItemsRequests(List.of(itemRequest));
        return itemRequest;
    }

    private void addItemsToItemsRequests(List<ItemRequest> requests) {
        Map<ItemRequest, List<Item>> itemsByItemRequest = itemRepository
                .findAllByRequestIn(requests)
                .stream()
                .collect(groupingBy(Item::getRequest, toList()));
        requests.forEach(i -> i.setItems(itemsByItemRequest.get(i)));
    }
}
