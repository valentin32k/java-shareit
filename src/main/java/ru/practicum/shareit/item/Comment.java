package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.shareit.user.User;

import javax.persistence.CollectionTable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Entity
@Table(name = "comments", schema = "public")
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotEmpty(message = "Comment text can not be empty")
    @Size(max = 4096, message = "Comment text must be shorter than 4096 characters")
    private String text;

    @ManyToOne
    @NotNull(message = "The comment must be related to the thing")
    @CollectionTable(name = "items", joinColumns = @JoinColumn(name = "id"))
    @JoinColumn(name = "item_id")
    private Item item;

    @ManyToOne
    @NotNull(message = "The field author can not be null")
    @CollectionTable(name = "users", joinColumns = @JoinColumn(name = "id"))
    @JoinColumn(name = "author_id")
    private User author;

    @NotNull(message = "The field created can not be null")
    private LocalDateTime created;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Comment comment = (Comment) o;
        return id == comment.id;
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
