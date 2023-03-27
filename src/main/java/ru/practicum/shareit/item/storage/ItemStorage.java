package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorage {
    List<Item> findAll();

    Item findById(long itemId);

    List<Item> search(String request);

    Item add(Item item);

    Item update(long itemId, Item user);

    void delete(long itemId);
}
