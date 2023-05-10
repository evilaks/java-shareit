package ru.practicum.shareit.booking.storage;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByBookerIdOrderByStartTimeDesc(Long bookerId, Pageable pageable);

    List<Booking> findAllByBookerIdAndStartTimeBeforeAndEndTimeAfterOrderByStartTimeDesc(Long bookerId,
                                                                                         LocalDateTime startTime,
                                                                                         LocalDateTime endTime,
                                                                                         Pageable pageable);

    List<Booking> findAllByBookerIdAndEndTimeBeforeOrderByStartTimeDesc(Long bookerId,
                                                                        LocalDateTime endTime,
                                                                        Pageable pageable);

    List<Booking> findAllByBookerIdAndStartTimeAfterOrderByStartTimeDesc(Long bookerId,
                                                                         LocalDateTime startTime,
                                                                         Pageable pageable);

    List<Booking> findAllByBookerIdAndStatusOrderByStartTimeDesc(Long bookerId,
                                                                 BookingStatus status,
                                                                 Pageable pageable);

    List<Booking> findAllByItemOwnerIdAndStartTimeBeforeAndEndTimeAfterOrderByStartTimeDesc(Long ownerId,
                                                                                            LocalDateTime startTime,
                                                                                            LocalDateTime endTime,
                                                                                            Pageable pageable);

    List<Booking> findAllByItemOwnerIdAndEndTimeBeforeOrderByStartTimeDesc(Long ownerId,
                                                                           LocalDateTime endTime,
                                                                           Pageable pageable);

    List<Booking> findAllByItemOwnerIdAndStartTimeAfterOrderByStartTimeDesc(Long ownerId,
                                                                            LocalDateTime startTime,
                                                                            Pageable pageable);

    List<Booking> findAllByItemOwnerIdAndStatusOrderByStartTimeDesc(Long ownerId,
                                                                    BookingStatus status,
                                                                    Pageable pageable);

    List<Booking> findAllByItemOwnerIdOrderByStartTimeDesc(Long ownerId,
                                                           Pageable pageable);

    Optional<Booking> findFirstByItemIdAndStartTimeBeforeAndStatusOrderByStartTimeDesc(Long itemId,
                                                                                       LocalDateTime endTime,
                                                                                       BookingStatus status);

    Optional<Booking> findFirstByItemIdAndStartTimeAfterAndStatusOrderByStartTimeAsc(Long itemId,
                                                                                     LocalDateTime startTime,
                                                                                     BookingStatus status);

    Boolean existsByItemIdAndBookerIdAndStatusAndEndTimeBefore(Long itemId,
                                                               Long userId,
                                                               BookingStatus status,
                                                               LocalDateTime endTime);
}
