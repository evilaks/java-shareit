package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;

import java.util.List;

public interface ItemService {
    ItemWithBookingsDto findById(long itemId, long userId);

    List<ItemDto> findAll(long userId);

    ItemDto add(long userId, ItemDto itemDto);

    ItemDto update(long userId, long itemId, ItemDto itemDto);

    List<ItemDto> search(long userId, String request);

    void delete(long userId, long itemId);
}
