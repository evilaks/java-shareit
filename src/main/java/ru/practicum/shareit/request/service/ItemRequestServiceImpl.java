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
import ru.practicum.shareit.util.exception.ValidationException;

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

        // throws 400 if item request is not valid
        if (!isValidItemRequest(itemRequestDto)) {
            throw new ValidationException("Item request is not valid");
        }

        ItemRequest itemRequest = itemRequestDtoMapper.toItemRequest(itemRequestDto, userDtoMapper.toUser(userDto));
        itemRequest.setCreated(LocalDateTime.now());

        itemRequest = itemRequestRepository.save(itemRequest);

        return itemRequestDtoMapper.toItemRequestDto(itemRequest);
    }

    @Override
    public List<ItemRequestDto> getUserItemRequests(Long userId) {
        // throws 404 if user not found
        userService.findById(userId);

        return itemRequestRepository.findAllByAuthorId(userId).stream()
                .map(itemRequestDtoMapper::toItemRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestDto> getOtherUsersItemRequests(Long userId, Integer from, Integer size) {
        // throws 404 if user not found
        userService.findById(userId);

        return itemRequestRepository.findAllByAuthorIdNot(userId, PageRequest.of(from, size, Sort.by("created")))
                .stream()
                .map(itemRequestDtoMapper::toItemRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequestDto getItemRequestById(Long userId, Long id) {
        return null;
    }

    private Boolean isValidItemRequest(ItemRequestDto itemRequestDto) {
        return itemRequestDto != null
                && itemRequestDto.getDescription() != null
                && itemRequestDto.getDescription().length() > 0;
    }
}
