package ru.practicum.shareit.user;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component
public class InMemoryUserStorage implements UserStorage {

    private final HashMap<Long, User> usersById = new HashMap<>();
    private long id = 0;

    @Override
    public User createUser(User user) {
        user.setId(++id);
        usersById.put(id, user);
        return user;
    }

    @Override
    public User getUserById(Long id) {
        return usersById.get(id);
    }

    @Override
    public List<User> getUsers() {
        return new ArrayList<>(usersById.values());
    }

    @Override
    public User updateUser(User user) {
        usersById.put(user.getId(), user);
        return user;
    }

    @Override
    public void removeUserById(Long id) {
        usersById.remove(id);
    }
}