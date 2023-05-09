package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.dto.BookingShort;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByBookerIdOrderByStartDesc(long userId);

    List<Booking> findAllByBookerIdAndStartIsAfterOrderByStartDesc(long bookerId, LocalDateTime dateTime);

    List<Booking> findAllByItemOwnerIdOrderByStartDesc(long userId);

    List<Booking> findAllByItemOwnerIdAndStartIsAfterOrderByStartDesc(long userId, LocalDateTime dateTime);

    List<Booking> findAllByBookerIdAndStatus(long ownerId, BookingStatus state);

    List<Booking> findAllByItemOwnerIdAndStatus(long userId, BookingStatus state);

    BookingShort findFirstByItemIdAndStatusOrderByStartAsc(long itemId, BookingStatus state);
    BookingShort findFirstByItemIdAndStartIsAfterAndStatusOrderByStartAsc(long itemId, LocalDateTime currentTime, BookingStatus state);

    List<BookingShort> findAllByItemOwnerId(long ownerId);
}