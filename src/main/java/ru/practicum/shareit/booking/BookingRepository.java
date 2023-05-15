package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByBookerIdOrderByStartDesc(long userId);

    List<Booking> findAllByBookerIdAndStartIsAfterOrderByStartDesc(
            long bookerId,
            LocalDateTime dateTime);

    List<Booking> findAllByItemOwnerIdOrderByStartDesc(long userId);

    List<Booking> findAllByItemOwnerIdAndStartIsAfterOrderByStartDesc(
            long userId,
            LocalDateTime dateTime);

    List<Booking> findAllByBookerIdAndStatus(
            long ownerId,
            BookingStatus state);

    List<Booking> findAllByItemOwnerIdAndStatus(
            long userId,
            BookingStatus state);

    Booking findFirstByItemIdAndStartIsBeforeAndStatusOrderByStartDesc(
            long itemId,
            LocalDateTime currentTime,
            BookingStatus state);

    Booking findFirstByItemIdAndStartIsAfterAndStatusOrderByStartAsc(
            long itemId,
            LocalDateTime currentTime,
            BookingStatus state);

    List<Booking> findAllByItemOwnerId(long ownerId);

    boolean existsBookingByBookerIdAndItemIdAndStatusAndEndIsBefore(
            long bookerId,
            long itemId,
            BookingStatus status,
            LocalDateTime currentTime);

    List<Booking> findAllByBookerIdAndStartIsBeforeAndEndIsAfter(
            long bookerId,
            LocalDateTime start,
            LocalDateTime end);

    List<Booking> findAllByItemOwnerIdAndStartIsBeforeAndEndIsAfter(
            long bookerId,
            LocalDateTime start,
            LocalDateTime end);

    List<Booking> findAllByBookerIdAndEndIsBeforeOrderByStartDesc(
            long bookerId,
            LocalDateTime end);

    List<Booking> findAllByItemOwnerIdAndEndIsBeforeOrderByStartDesc(
            long bookerId,
            LocalDateTime end);
}