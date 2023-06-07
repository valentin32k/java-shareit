package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.exceptions.BadMethodArgumentsException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.data.domain.Sort.Direction.ASC;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRequestRepository itemRequestRepository;
    @InjectMocks
    ItemServiceImpl service;

    @Test
    void createItemWithRequest_shouldCreateItem() {
        Item item = firstItemInit();

        Mockito.when(userRepository.findById(anyLong())).thenReturn(Optional.of(initUser()));
        Mockito.when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.of(itemRequest()));
        Mockito.when(itemRepository.save(any())).thenReturn(item);

        assertEquals(item, service.createItem(item, 1L, 1L));
        verify(userRepository, times(1)).findById(any());
        verify(itemRequestRepository, times(1)).findById(any());
        verify(itemRepository, times(1)).save(any());
    }

    @Test
    void createItemWithBadUser_shouldThrowNotFoundException() {
        Mockito.when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class, () -> service.createItem(firstItemInit(), 1L, 1L));
        assertEquals("User with id = 1 is not found", exception.getMessage());
    }

    @Test
    void createItemWithoutRequest_shouldThrowNotFoundException() {
        Item item = secondItemInit();

        Mockito.when(userRepository.findById(anyLong())).thenReturn(Optional.of(initUser()));
        Mockito.when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.empty());
        Mockito.when(itemRepository.save(any())).thenReturn(item);

        assertEquals(item, service.createItem(item, 1L, 1L));
        verify(userRepository, times(1)).findById(any());
        verify(itemRequestRepository, times(1)).findById(any());
        verify(itemRepository, times(1)).save(any());
    }

    @Test
    void updateItem_shouldUpdateName() {
        Item item = firstItemInit();
        Item updatedItemField = Item.builder()
                .name("name2")
                .build();

        Mockito.when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        Item returnedItem = service.updateItem(updatedItemField, item.getId(), item.getOwner().getId());
        item.setName("name2");

        assertEquals(item, returnedItem);
    }

    @Test
    void updateItem_shouldUpdateDescription() {
        Item item = firstItemInit();
        Item updatedItemField = Item.builder()
                .description("desc2")
                .build();

        Mockito.when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        Item returnedItem = service.updateItem(updatedItemField, item.getId(), item.getOwner().getId());
        item.setDescription("desc2");

        assertEquals(item, returnedItem);
    }

    @Test
    void updateItem_shouldUpdateAvailable() {
        Item item = firstItemInit();
        Item updatedItemField = Item.builder()
                .available(false)
                .build();

        Mockito.when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        Item returnedItem = service.updateItem(updatedItemField, item.getId(), item.getOwner().getId());
        item.setAvailable(false);

        assertEquals(item, returnedItem);
    }

    @Test
    void updateItem_shouldThrowNotFoundException() {
        Item item = firstItemInit();

        Mockito.when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        NotFoundException exception = assertThrows(
                NotFoundException.class, () -> service.updateItem(item, 1L, 11L));
        assertEquals("Only the owner can update a post", exception.getMessage());
    }

    @Test
    void getItemById_shouldReturnItem() {
        Item item = firstItemInit();

        Mockito.when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        Mockito.when(commentRepository.findAllByItemId(1L)).thenReturn(initComments());

        item.setComments(initComments());
        assertEquals(item, service.getItemById(1L, 1L));

        verify(itemRepository, times(1)).findById(anyLong());
        verify(commentRepository, times(1)).findAllByItemId(anyLong());
    }

    @Test
    void getItemById_shouldThrowNotFoundException() {
        Mockito.when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class, () -> service.getItemById(1L, 11L));
        assertEquals("Item with id = 1 is not found", exception.getMessage());
    }

    @Test
    void getUserItems_shouldReturnListOfItems() {
        Item item1 = firstItemInit();
        Item item2 = secondItemInit();

        Mockito.when(itemRepository.findAllByOwnerId(1L, PageRequest.of(0 / 20, 20, Sort.by(ASC, "id"))))
                .thenReturn(new PageImpl<>(List.of(item1, item2)));

        assertEquals(List.of(item1, item2), service.getUserItems(1L, 0, 20));
        verify(itemRepository, times(1)).findAllByOwnerId(anyLong(), any());
    }

    @Test
    void getUserItemsWithWrongFrom_shouldThrowBadMethodArgumentsException() {
        NullPointerException exception = assertThrows(
                NullPointerException.class, () -> service.getUserItems(1L, -1, 20));
    }

    @Test
    void getUserItemsWithWrongSize_shouldThrowBadMethodArgumentsException() {
        assertThrows(ArithmeticException.class, () -> service.getUserItems(1L, 0, 0));
    }

    @Test
    void findItemsWithText_shouldReturnListOfItems() {
        Item item1 = firstItemInit();
        Item item2 = secondItemInit();

        Mockito.when(itemRepository.search(anyString(), any()))
                .thenReturn(new PageImpl<>(List.of(item1, item2)));

        assertEquals(List.of(item1, item2), service.findItemsWithText("Hello", 0, 20));
        verify(itemRepository, times(1)).search(anyString(), any());
    }

    @Test
    void findItemsWithEmptyText_shouldReturnEmptyList() {
        assertEquals(Collections.emptyList(), service.findItemsWithText("", 0, 20));
    }

    @Test
    void findItemsWithTextWithWrongFrom_shouldThrowBadMethodArgumentsException() {
        NullPointerException exception = assertThrows(
                NullPointerException.class, () -> service.findItemsWithText("text", -1, 20));
    }

    @Test
    void findItemsWithTextWithWrongSize_shouldThrowBadMethodArgumentsException() {
        ArithmeticException exception = assertThrows(
                ArithmeticException.class, () -> service.findItemsWithText("text", 1, 0));
    }

    @Test
    void createComment_shouldCreateComment() {
        Comment comment = initComments().get(0);

        Mockito.when(commentRepository.save(any())).thenReturn(comment);
        Mockito.when(bookingRepository
                        .existsBookingByBookerIdAndItemIdAndStatusAndEndIsBefore(anyLong(), anyLong(), any(), any()))
                .thenReturn(true);
        Mockito.when(itemRepository.findById(anyLong())).thenReturn(Optional.of(firstItemInit()));
        Mockito.when(userRepository.findById(anyLong())).thenReturn(Optional.of(initUser()));

        assertEquals(comment, service.createComment(comment, 1L, 1L));
        verify(commentRepository, times(1)).save(any());
        verify(bookingRepository, times(1))
                .existsBookingByBookerIdAndItemIdAndStatusAndEndIsBefore(anyLong(), anyLong(), any(), any());
        verify(itemRepository, times(1)).findById(anyLong());
        verify(userRepository, times(1)).findById(anyLong());
    }

    @Test
    void createComment_shouldThrowBadMethodArgumentsException() {
        Mockito.when(bookingRepository
                        .existsBookingByBookerIdAndItemIdAndStatusAndEndIsBefore(anyLong(), anyLong(), any(), any()))
                .thenReturn(false);

        BadMethodArgumentsException exception = assertThrows(
                BadMethodArgumentsException.class, () -> service.createComment(initComments().get(0), 1, 0));
        assertEquals("Comment not added", exception.getMessage());
    }

    @Test
    void getItemComments_shouldReturnListOfComments() {
        List<Comment> comments = initComments();

        Mockito.when(commentRepository.findAllByItemId(anyLong())).thenReturn(comments);

        assertEquals(comments, service.getItemComments(1L));
    }

    private Item firstItemInit() {
        return Item.builder()
                .id(1)
                .name("name")
                .description("descriiption")
                .available(true)
                .owner(initUser())
                .request(itemRequest())
                .build();
    }

    private Item secondItemInit() {
        return Item.builder()
                .id(1)
                .name("name")
                .description("descriiption")
                .available(true)
                .owner(initUser())
                .build();
    }

    private User initUser() {
        return User.builder()
                .id(1L)
                .name("Иван")
                .email("vanya@email.ru")
                .build();
    }

    private ItemRequest itemRequest() {
        return ItemRequest.builder()
                .id(1)
                .description("requestDescription1")
                .requestor(initUser())
                .created(LocalDateTime.now())
                .build();
    }

    private List<Comment> initComments() {
        return List.of(Comment.builder()
                .id(1L)
                .text("commentText")
                .item(firstItemInit())
                .author(initUser())
                .build());
    }
}