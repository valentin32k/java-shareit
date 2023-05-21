package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.exceptions.BadMethodArgumentsException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.data.domain.Sort.Direction.DESC;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private ItemRequestRepository itemRequestRepository;

    @InjectMocks
    private ItemRequestServiceImpl service;

    @Test
    void createItemRequest_shouldCreateAUser() {
        ItemRequest request = firstItemRequest();

        Mockito.when(userRepository.findById(any())).thenReturn(Optional.of(User.builder().build()));
        Mockito.when(itemRequestRepository.save(request)).thenReturn(request);

        assertEquals(request, service.createItemRequest(request, 1L));
        verify(userRepository, times(1)).findById(any());
        verify(itemRequestRepository, times(1)).save(any());
    }

    @Test
    void createItemRequest_shouldThrowNotFoundException() {
        Mockito.when(userRepository.findById(any())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class, () -> service.createItemRequest(firstItemRequest(), 1L));
        assertEquals("User with id = 1 is not found", exception.getMessage());
    }

    @Test
    void getUserItemRequests_shouldReturnItemRequests() {
        ItemRequest itemRequest1 = firstItemRequest();
        ItemRequest itemRequest2 = secondItemRequest();

        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(initUser()));
        Mockito.when(itemRequestRepository.findAllByRequestorId(1L, Sort.by(DESC, "created")))
                .thenReturn(List.of(itemRequest2, itemRequest1));

        assertEquals(List.of(itemRequest2, itemRequest1), service.getUserItemRequests(1L));
        verify(userRepository, times(1)).findById(any());
        verify(itemRequestRepository, times(1)).findAllByRequestorId(anyLong(), any());
    }

    @Test
    void getUserItemRequests_shouldThrowNotFoundException() {
        Mockito.when(userRepository.findById(any())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> service.getUserItemRequests(1L));
        assertEquals("User with id = 1 is not found", exception.getMessage());
    }

    @Test
    void getItemRequests_shouldReturnListOfTwoItems() {
        ItemRequest itemRequest1 = firstItemRequest();
        ItemRequest itemRequest2 = secondItemRequest();
        Mockito.when(
                        itemRequestRepository
                                .findAllByRequestorIdNot(55L,
                                        PageRequest.of(0, 20, Sort.by(DESC, "created"))))
                .thenReturn(new PageImpl<>(List.of(itemRequest2, itemRequest1)));

        assertEquals(List.of(itemRequest2, itemRequest1), service.getItemRequests(0, 20, 55L));
        verify(itemRequestRepository, times(1)).findAllByRequestorIdNot(anyLong(), any());
    }

    @Test
    void getItemRequestsWithBadFrom_shouldThrowBadMethodArgumentsException() {
        BadMethodArgumentsException exception = assertThrows(BadMethodArgumentsException.class, () -> service.getItemRequests(-1, 5, 1L));
        assertEquals("Items request size must not be less than 1 " +
                "and start item number must not be less then 0", exception.getMessage());
    }

    @Test
    void getItemRequestsWithBadSize_shouldThrowBadMethodArgumentsException() {
        BadMethodArgumentsException exception = assertThrows(BadMethodArgumentsException.class, () -> service.getItemRequests(0, 0, 1L));
        assertEquals("Items request size must not be less than 1 " +
                "and start item number must not be less then 0", exception.getMessage());
    }

    @Test
    void getItemRequestById_shouldReturnItem() {
        ItemRequest itemRequest1 = firstItemRequest();

        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(initUser()));
        Mockito.when(itemRequestRepository.findById(1L)).thenReturn(Optional.of(itemRequest1));

        assertEquals(itemRequest1, service.getItemRequestById(1L, 1L));

        verify(userRepository, times(1)).findById(anyLong());
        verify(itemRequestRepository, times(1)).findById(anyLong());
    }

    @Test
    void getItemRequestByIdWithBadUserId_shouldThrowNotFoundException() {
        Mockito.when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class, () -> service.getItemRequestById(2L, 1L));
        assertEquals("User with id = 1 is not found", exception.getMessage());
    }

    @Test
    void getItemRequestByIdWithBadItemId_shouldThrowNotFoundException() {
        Mockito.when(userRepository.findById(anyLong())).thenReturn(Optional.of(initUser()));
        Mockito.when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class, () -> service.getItemRequestById(2L, 1L));
        assertEquals("Item request with id = 2 is not found", exception.getMessage());
    }

    private User initUser() {
        return User.builder()
                .id(1L)
                .name("Иван")
                .email("vanya@email.ru")
                .build();
    }

    private ItemRequest firstItemRequest() {
        Item item1 = Item.builder()
                .id(1)
                .name("name")
                .description("description")
                .available(true)
                .build();

        Item item2 = Item.builder()
                .id(2)
                .name("name2")
                .description("description2")
                .available(true)
                .build();

        return ItemRequest.builder()
                .id(1)
                .description("requestDescription1")
                .requestor(initUser())
                .created(LocalDateTime.now())
                .items(List.of(item1, item2))
                .build();
    }

    private ItemRequest secondItemRequest() {
        return ItemRequest.builder()
                .id(2)
                .description("requestDescription2")
                .requestor(initUser())
                .created(LocalDateTime.now())
                .build();
    }
}