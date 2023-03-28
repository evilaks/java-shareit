package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorage {
    List<Item> findAllByOwnerId(long ownerId);

    Item findById(long itemId);

    List<Item> search(String request);

    Item add(Item item);

    Item update(long itemId, Item item);

    void delete(long itemId);
}
