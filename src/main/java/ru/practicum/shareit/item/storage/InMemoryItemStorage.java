package ru.practicum.shareit.item.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class InMemoryItemStorage implements ItemStorage {

    private final HashMap<Long, Item> items = new HashMap<>();
    private Long id = 0L;

    @Override
    public List<Item> findAll() {
        log.debug("findAll method called");
        return new ArrayList<>(items.values());
    }

    @Override
    public Item findById(long itemId) {
        log.debug("findById method called with itemId={}", itemId);
        return items.getOrDefault(itemId, null);
    }

    @Override
    public List<Item> search(String request) {
        log.debug("search method called with request={}", request);
        // todo search
        return null;
    }

    @Override
    public Item add(Item item) {
        log.debug("add method called with {}", item);
        Long itemId = this.generateId();
        item.setId(itemId);
        items.put(itemId, item);
        return item;
    }

    @Override
    public Item update(long itemId, Item item) {
        log.debug("update method called with itemId={} and {}", itemId, item);
        items.put(itemId, item);
        return item;
    }

    @Override
    public void delete(long itemId) {
        log.debug("delete method called with itemId={}", itemId);
        items.remove(itemId);
    }

    private Long generateId() {
        return ++id;
    }
}
