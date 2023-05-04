package ru.practicum.shareit.request.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.request.model.ItemRequest;

import org.springframework.data.domain.Pageable;
import java.util.List;

@Repository
public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

    List<ItemRequest> findAllByAuthorId(Long authorId);

    List<ItemRequest> findAllByAuthorIdNot(Long authorId, Pageable pageable);
}
