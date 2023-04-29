package ru.practicum.shareit.booking.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByBookerIdOrderByStartTimeDesc(Long bookerId);

    List<Booking> findAllByBookerIdAndStartTimeBeforeAndEndTimeAfterOrderByStartTimeDesc(Long bookerId,
                                                                     LocalDateTime startTime,
                                                                     LocalDateTime endTime);

    List<Booking> findAllByBookerIdAndEndTimeBeforeOrderByStartTimeDesc(Long bookerId, LocalDateTime endTime);

    List<Booking> findAllByBookerIdAndStartTimeAfterOrderByStartTimeDesc(Long bookerId, LocalDateTime startTime);

    List<Booking> findAllByBookerIdAndStatusOrderByStartTimeDesc(Long bookerId, BookingStatus status);

    List<Booking> findAllByItemOwnerIdAndStartTimeBeforeAndEndTimeAfterOrderByStartTimeDesc(Long ownerId,
                                                                     LocalDateTime startTime,
                                                                     LocalDateTime endTime);

    List<Booking> findAllByItemOwnerIdAndEndTimeBeforeOrderByStartTimeDesc(Long ownerId, LocalDateTime endTime);

    List<Booking> findAllByItemOwnerIdAndStartTimeAfterOrderByStartTimeDesc(Long ownerId, LocalDateTime startTime);

    List<Booking> findAllByItemOwnerIdAndStatusOrderByStartTimeDesc(Long ownerId, BookingStatus status);

    List<Booking> findAllByItemOwnerIdOrderByStartTimeDesc(Long ownerId);
}
