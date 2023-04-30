package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.item.model.Comment;

import java.util.List;

public interface ItemService {
    ItemWithBookingsDto findById(long itemId, long userId);

    List<ItemWithBookingsDto> findAll(long userId);

    ItemDto add(long userId, ItemDto itemDto);

    ItemDto update(long userId, long itemId, ItemDto itemDto);

    List<ItemDto> search(long userId, String request);

    CommentDto addComment(long userId, long itemId, Comment comment);

    void delete(long userId, long itemId);
}
