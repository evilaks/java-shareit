package ru.practicum.shareit.item.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class InMemoryItemStorage implements ItemStorage {

    private final HashMap<Long, Item> items = new HashMap<>();
    private Long id = 0L;

    @Override
    public List<Item> findAll() {
        return new ArrayList<>(items.values());
    }

    @Override
    public Item findById(long itemId) {
        return items.get(itemId);
    }

    @Override
    public List<Item> search(String request) {
        return null;
    }

    @Override
    public Item add(Item item) {
        Long itemId = this.generateId();
        item.setId(itemId);
        items.put(itemId, item);
        return item;
    }

    @Override
    public Item update(long itemId, Item item) {
        items.put(itemId, item);
        return item;
    }

    @Override
    public void delete(long itemId) {
        items.remove(itemId);
    }

    private Long generateId() {
        return ++id;
    }
}
