package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.storage.ItemRequestRepository;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;

    @Override
    public ItemRequestDto create(ItemRequestDto itemRequestDto) {
        return null;
    }

    @Override
    public List<ItemRequestDto> getUserItemRequests(Long userId) {
        return null;
    }

    @Override
    public List<ItemRequestDto> getOtherUsersItemRequests() {
        return null;
    }

    @Override
    public ItemRequestDto getItemRequestById(Long id) {
        return null;
    }
}
