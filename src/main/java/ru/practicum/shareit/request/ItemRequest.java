package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import javax.persistence.CollectionTable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "requests", schema = "public")
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ItemRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank(message = "The field description can not be blank")
    @Size(max = 1024, message = "Description must be shorter than 1024 characters")
    private String description;

    @ManyToOne
    @NotNull(message = "The field requestor can not be null")
    @CollectionTable(name = "users", joinColumns = @JoinColumn(name = "id"))
    @JoinColumn(name = "requestor_id")
    private User requestor;

    @NotNull(message = "The field created can not be null")
    private LocalDateTime created;

    @Transient
    private List<Item> items;
}
