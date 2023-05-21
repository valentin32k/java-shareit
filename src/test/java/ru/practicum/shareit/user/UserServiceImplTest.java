package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exceptions.NotFoundException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @Mock
    private UserRepository mockUserRepository;

    @InjectMocks
    private UserServiceImpl service;

    @Test
    void createUser_shouldCreateAUser() {
        User user = initUser();
        Mockito.when(mockUserRepository.save(user)).thenReturn(user);

        assertEquals(user, service.createUser(user));
        verify(mockUserRepository, times(1)).save(any());
    }

    @Test
    void updateUser_shouldUpdateUserName() {
        User user = initUser();
        User updatedUserField = User.builder()
                .id(1L)
                .name("Борис")
                .build();
        Mockito.when(mockUserRepository.findById(1L)).thenReturn(Optional.of(user));
        user.setName("Борис");
        assertEquals(user, service.updateUser(updatedUserField));
    }

    @Test
    void updateUser_shouldUpdateUserEmail() {
        User user = initUser();
        User updatedUserField = User.builder()
                .id(1L)
                .email("ivan@email.ru")
                .build();
        Mockito.when(mockUserRepository.findById(1L)).thenReturn(Optional.of(user));
        user.setEmail("ivan@email.ru");
        assertEquals(user, service.updateUser(updatedUserField));
    }

    @Test
    void getUserById_shouldReturnUserById() {
        User user = initUser();
        Mockito.when(mockUserRepository.findById(1L)).thenReturn(Optional.of(user));
        assertEquals(user, service.updateUser(user));
        verify(mockUserRepository, times(1)).findById(any());
    }

    @Test
    void getUserById_shouldThrowIfUserDoesNotExists() {
        Mockito.when(mockUserRepository.findById(any())).thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class, () -> service.getUserById(1L));
        assertEquals("User with id = 1 is not found", exception.getMessage());
    }

    @Test
    void getUsers_shouldReturnListOfUsers() {
        User user1 = initUser();
        User user2 = User.builder()
                .id(2)
                .name("Петя")
                .email("petr@email.ru")
                .build();
        Mockito.when(mockUserRepository.findAll()).thenReturn(List.of(user1, user2));
        assertEquals(List.of(user1, user2), service.getUsers());
        verify(mockUserRepository, times(1)).findAll();
    }

    @Test
    void removeUserById_shouldRemoveUser() {
        service.removeUserById(1L);
        verify(mockUserRepository, times(1)).deleteById(any());
    }

    private User initUser() {
        return User.builder()
                .id(1L)
                .name("Иван")
                .email("vanya@email.ru")
                .build();
    }
}