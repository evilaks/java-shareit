package ru.practicum.shareit.item.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Set;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findAllByOwnerIdOrderById(long ownerId);

    Set<Item> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndIsAvailable(String name, String description, Boolean isAvailable);

}
