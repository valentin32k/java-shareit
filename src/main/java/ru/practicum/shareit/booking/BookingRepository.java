package ru.practicum.shareit.booking;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.Item;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    Page<Booking> findAllByBookerId(long userId, PageRequest pageRequest);

    Page<Booking> findAllByBookerIdAndStartIsBeforeAndEndIsAfter(
            long bookerId,
            LocalDateTime start,
            LocalDateTime end,
            PageRequest pageRequest);

    Page<Booking> findAllByBookerIdAndEndIsBefore(
            long bookerId,
            LocalDateTime end,
            PageRequest pageRequest);

    Page<Booking> findAllByBookerIdAndStartIsAfter(
            long bookerId,
            LocalDateTime dateTime,
            PageRequest pageRequest);

    Page<Booking> findAllByBookerIdAndStatus(
            long ownerId,
            BookingStatus state,
            PageRequest pageRequest);

    Page<Booking> findAllByItemOwnerId(long userId, PageRequest pageRequest);

    Page<Booking> findAllByItemOwnerIdAndStartIsBeforeAndEndIsAfter(
            long bookerId,
            LocalDateTime start,
            LocalDateTime end,
            PageRequest pageRequest);

    Page<Booking> findAllByItemOwnerIdAndEndIsBefore(
            long bookerId,
            LocalDateTime end,
            PageRequest pageRequest);

    Page<Booking> findAllByItemOwnerIdAndStartIsAfter(
            long userId,
            LocalDateTime dateTime,
            PageRequest pageRequest);

    Page<Booking> findAllByItemOwnerIdAndStatus(
            long userId,
            BookingStatus state,
            PageRequest pageRequest);

    Booking findFirstByItemIdAndStartLessThanEqualAndStatusOrderByStartDesc(
            long itemId,
            LocalDateTime currentTime,
            BookingStatus state);

    Booking findFirstByItemIdAndStartIsAfterAndStatusOrderByStartAsc(
            long itemId,
            LocalDateTime currentTime,
            BookingStatus state);

    boolean existsBookingByBookerIdAndItemIdAndStatusAndEndIsBefore(
            long bookerId,
            long itemId,
            BookingStatus status,
            LocalDateTime currentTime);

    List<Booking> findByItemIn(List<Item> items, Sort sortParam);
}