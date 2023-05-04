package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto createItemRequest(Long userId, ItemRequestDto itemRequestDto);

    List<ItemRequestDto> getUserItemRequests(Long userId);

    List<ItemRequestDto> getOtherUsersItemRequests(Long userId, Integer from, Integer size);

    ItemRequestDto getItemRequestById(Long userId, Long id);
}
