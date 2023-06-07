package ru.practicum.shareit.user;

import java.util.List;

public interface UserService {

    /**
     * Creates a new user
     *
     * @param user
     * @return new user
     */
    User createUser(User user);

    /**
     * Updates the user
     *
     * @param user
     * @return updated user
     */
    User updateUser(User user);

    /**
     * Returns user by id
     * If the user is not found throws NotFoundException
     *
     * @param userId
     * @return user by id
     */
    User getUserById(Long userId);

    /**
     * Returns a list of all users
     *
     * @return list of all users
     */
    List<User> getUsers();

    /**
     * Removes user by id
     *
     * @param userId
     */
    void removeUserById(Long userId);
}
