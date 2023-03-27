package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.storage.ItemStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemStorage itemStorage;

    @Override
    public ItemDto findById(long itemId, long userId) {
        return null;
    }

    @Override
    public List<ItemDto> findAll(long userId) {
        return null;
    }

    @Override
    public ItemDto add(long userId, ItemDto itemDto) {
        return null;
    }

    @Override
    public ItemDto update(long userId, long itemId, ItemDto itemDto) {
        return null;
    }

    @Override
    public void delete(long userId, long itemId) {

    }
}
