package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class UserServiceImplTestIntegration {

    private final EntityManager em;
    private final UserServiceImpl service;

    @Test
    void createUser() {
        User user = initUser();
        service.createUser(user);

        TypedQuery<User> query = em.createQuery("select u from User u where u.email = :email", User.class);
        User newUser = query.setParameter("email", user.getEmail()).getSingleResult();

        assertThat(newUser.getId(), notNullValue());
        assertThat(newUser.getName(), equalTo(user.getName()));
        assertThat(newUser.getEmail(), equalTo(user.getEmail()));
    }

    @Test
    void updateUser() {
        User user = initUser();
        user = service.createUser(user);
        user.setName("Name2");
        service.updateUser(user);

        TypedQuery<User> query = em.createQuery("select u from User u where u.email = :email", User.class);
        List<User> newUsersList = query.setParameter("email", user.getEmail()).getResultList();

        assertThat(newUsersList.size(), is(1));
        User newUser = newUsersList.get(0);

        assertThat(newUser.getId(), equalTo(user.getId()));
        assertThat(newUser.getName(), equalTo("Name2"));
        assertThat(newUser.getEmail(), equalTo(user.getEmail()));
    }

    @Test
    void getUserById() {
        User user = initUser();
        user = service.createUser(user);

        TypedQuery<User> query = em.createQuery("select u from User u where u.id = :id", User.class);
        User newUser = query.setParameter("id", user.getId()).getSingleResult();

        assertThat(newUser.getId(), equalTo(user.getId()));
        assertThat(newUser.getName(), equalTo(user.getName()));
        assertThat(newUser.getEmail(), equalTo(user.getEmail()));
    }

    @Test
    void getUsers() {
        User user1 = initUser();
        User user2 = User.builder()
                .id(2)
                .name("Name2")
                .email("e2@m.ru")
                .build();
        service.createUser(user1);
        service.createUser(user2);

        List<User> returnedUsers = service.getUsers();
        User returnedUser1 = returnedUsers.get(0);
        User returnedUser2 = returnedUsers.get(1);

        assertThat(returnedUsers.size(), equalTo(2));
        assertThat(returnedUser1.getId(), notNullValue());
        assertThat(returnedUser1.getName(), equalTo(user1.getName()));
        assertThat(returnedUser1.getEmail(), equalTo(user1.getEmail()));
        assertThat(returnedUser2.getId(), notNullValue());
        assertThat(returnedUser2.getName(), equalTo(user2.getName()));
        assertThat(returnedUser2.getEmail(), equalTo(user2.getEmail()));
    }

    @Test
    void removeUserById() {
        User user = initUser();
        user = service.createUser(user);

        TypedQuery<User> query = em.createQuery("select u from User u where u.email = :email", User.class);
        List<User> newUsersList = query.setParameter("email", user.getEmail()).getResultList();

        service.removeUserById(user.getId());
        query = em.createQuery("select u from User u where u.email = :email", User.class);
        List<User> newUsersList2 = query.setParameter("email", user.getEmail()).getResultList();

        assertThat(newUsersList.size(), is(1));
        assertThat(newUsersList2.size(), is(0));
    }

    private User initUser() {
        return User.builder()
                .name("Name")
                .email("email@f.ru")
                .build();
    }
}