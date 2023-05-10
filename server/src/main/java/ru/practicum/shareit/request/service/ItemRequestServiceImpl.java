package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoMapper;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.util.exception.NotFoundException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final ItemRequestDtoMapper itemRequestDtoMapper;
    private final UserService userService;
    private final UserDtoMapper userDtoMapper;

    @Override
    @Transactional
    public ItemRequestDto createItemRequest(Long userId, ItemRequestDto itemRequestDto) {
        // throws 404 if user not found
        UserDto userDto = userService.findById(userId);

        ItemRequest itemRequest = itemRequestDtoMapper.toItemRequest(itemRequestDto, userDtoMapper.toUser(userDto));
        itemRequest.setCreated(LocalDateTime.now());

        itemRequest = itemRequestRepository.save(itemRequest);

        return itemRequestDtoMapper.toItemRequestDto(itemRequest, itemRequest.getItems());
    }

    @Override
    public List<ItemRequestDto> getUserItemRequests(Long userId) {
        // throws 404 if user not found
        userService.findById(userId);

        return itemRequestRepository.findAllByAuthorId(userId).stream()
                .map(i -> itemRequestDtoMapper.toItemRequestDto(i, i.getItems()))
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestDto> getOtherUsersItemRequests(Long userId, Integer from, Integer size) {
        // throws 404 if user not found
        userService.findById(userId);

        return itemRequestRepository.findAllByAuthorIdNot(userId, PageRequest.of(from, size, Sort.by("created")))
                .stream()
                .map(i -> itemRequestDtoMapper.toItemRequestDto(i, i.getItems()))
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequestDto getItemRequestById(Long userId, Long id) {
        // throws 404 if user not found
        userService.findById(userId);

        return itemRequestRepository.findById(id)
                .map(i -> itemRequestDtoMapper.toItemRequestDto(i, i.getItems()))
                .orElseThrow(() -> new NotFoundException("Item request not found"));
    }
}
