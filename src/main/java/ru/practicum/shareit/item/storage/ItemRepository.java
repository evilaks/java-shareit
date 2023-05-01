package ru.practicum.shareit.item.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Set;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findAllByOwnerIdOrderById(long ownerId);

    Set<Item> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndIsAvailable(String name, String description, Boolean isAvailable);

}
