package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoMapper;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.util.exception.NotAllowedException;
import ru.practicum.shareit.util.exception.NotFoundException;
import ru.practicum.shareit.util.exception.ValidationException;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemStorage itemStorage;
    private final UserService userService;
    private final ItemDtoMapper itemDtoMapper;
    private final UserDtoMapper userDtoMapper;

    @Override
    public ItemDto findById(long itemId, long userId) {
        userService.findById(userId); // throws 404 if user not found
        Item item = itemStorage.findById(itemId);
        if (item != null) {
            return itemDtoMapper.toItemDto(item);
        } else throw new NotFoundException("Item not found");
    }

    @Override
    public List<ItemDto> findAll(long userId) {
        userService.findById(userId); // throws 404 if user not found
        return itemStorage.findAllByOwnerId(userId).stream()
                .map(itemDtoMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto add(long userId, ItemDto itemDto) {
        UserDto owner = userService.findById(userId); // throws 404 if user not found
        if (this.isValidItem(itemDto)) {
            Item newItem = itemStorage.add(itemDtoMapper.toItem(itemDto, userDtoMapper.toUser(owner)));
            itemDto.setId(newItem.getId());
            return itemDto;
        } else throw new ValidationException("Invalid item received");
    }

    @Override
    public ItemDto update(long userId, long itemId, ItemDto itemDto) {

        userService.findById(userId); // throws 404 if user not found
        Item itemToUpdate = itemStorage.findById(itemId);
        if (itemToUpdate == null) throw new NotFoundException("Item not found");
        if (itemToUpdate.getOwner().getId() != userId) throw new NotAllowedException("Item update is not allowed to that user");

        if (itemDto.getName() != null) itemToUpdate.setName(itemDto.getName());
        if (itemDto.getDescription() != null) itemToUpdate.setDescription(itemDto.getDescription());
        if (itemDto.getAvailable() != null) itemToUpdate.setIsAvailable(itemDto.getAvailable());

        return itemDtoMapper.toItemDto(itemStorage.update(itemId, itemToUpdate));
    }

    @Override
    public List<ItemDto> search(long userId, String request) {
        userService.findById(userId); // throws 404 if user not found
        if (request.isBlank()) return new ArrayList<>();
        return itemStorage.search(request).stream()
                .map(itemDtoMapper::toItemDto)
                .sorted(Comparator.comparing(ItemDto::getId))
                .collect(Collectors.toList());
    }

    @Override
    public void delete(long userId, long itemId) {
        userService.findById(userId); // throws 404 if user not found
        Item itemToDelete = itemStorage.findById(itemId);
        if (itemToDelete == null) throw new NotFoundException("Item not found");
        if (itemToDelete.getOwner().getId() != userId) throw new NotAllowedException("Item delete is not allowed to that user");

        itemStorage.delete(itemId);
    }

    private boolean isValidItem(ItemDto itemDto) {
        if (itemDto.getName() == null || itemDto.getName().isBlank()) {
            return false;
        }

        if (itemDto.getDescription() == null || itemDto.getName().isBlank()) {
            return false;
        }

        if (itemDto.getAvailable() == null) {
            return false;
        }

        return true;
    }

}
