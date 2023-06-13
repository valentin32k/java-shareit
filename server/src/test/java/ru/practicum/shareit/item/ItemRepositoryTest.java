package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.user.User;

import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.data.domain.Sort.Direction.ASC;

@DataJpaTest
class ItemRepositoryTest {
    @Autowired
    private TestEntityManager em;
    @Autowired
    private ItemRepository repository;

    @Test
    void search() {
        User user = em.persist(User.builder()
                .name("Vasiliy")
                .email("vasya@email.ru")
                .build());
        Item tvItem = em.persist(Item.builder()
                .name("TV")
                .description("With Alice remote control")
                .available(true)
                .owner(user)
                .build());
        Item bikeItem = em.persist(Item.builder()
                .name("Мотоцикл")
                .description("Очень быстрый мотоцикл")
                .available(true)
                .owner(user)
                .build());

        Item tvFindRes = repository
                .search("TV", PageRequest.of(0, 20, Sort.by(ASC, "id")))
                .stream()
                .collect(Collectors.toList())
                .get(0);
        assertThat(tvItem, equalTo(tvFindRes));

        tvFindRes = repository
                .search("Alice", PageRequest.of(0, 20, Sort.by(ASC, "id")))
                .stream()
                .collect(Collectors.toList())
                .get(0);
        assertThat(tvItem, equalTo(tvFindRes));

        Item bikeFindRes = repository
                .search("Мотоцикл", PageRequest.of(0, 20, Sort.by(ASC, "id")))
                .stream()
                .collect(Collectors.toList())
                .get(0);
        assertThat(bikeItem, equalTo(bikeFindRes));

        bikeFindRes = repository
                .search("быстрый", PageRequest.of(0, 20, Sort.by(ASC, "id")))
                .stream()
                .collect(Collectors.toList())
                .get(0);
        assertThat(bikeItem, equalTo(bikeFindRes));
    }
}