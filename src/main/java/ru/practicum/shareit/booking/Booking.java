package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "bookings", schema = "public")
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "start_date")
    @NotNull(message = "The field start date can not be null")
    private LocalDateTime start;

    @Column(name = "end_date")
    @NotNull(message = "The field end date can not be null")
    private LocalDateTime end;

    @ManyToOne
    @NotNull(message = "The field item can not be null")
    @CollectionTable(name = "items", joinColumns = @JoinColumn(name = "id"))
    @JoinColumn(name = "item_id")
    private Item item;

    @ManyToOne
    @NotNull(message = "The field booker can not be null")
    @CollectionTable(name = "users", joinColumns = @JoinColumn(name = "id"))
    @JoinColumn(name = "booker_id")
    private User booker;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "The field status can not be null")
    private BookingStatus status;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Booking booking = (Booking) o;
        return id == booking.id;
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

}
