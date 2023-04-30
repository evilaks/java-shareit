package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDtoMapper;
import ru.practicum.shareit.booking.dto.ItemBookingDto;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoMapper;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoMapper;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.util.exception.NotAllowedException;
import ru.practicum.shareit.util.exception.NotFoundException;
import ru.practicum.shareit.util.exception.ValidationException;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemStorage itemStorage;
    private final ItemRepository itemRepository;
    private final UserService userService;
    private final ItemDtoMapper itemDtoMapper;
    private final UserDtoMapper userDtoMapper;
    private final BookingDtoMapper bookingDtoMapper;
    private final BookingRepository bookingRepository;

    @Override
    public ItemWithBookingsDto findById(long itemId, long userId) {

        // throws 404 if user not found
        userService.findById(userId);

        Item item = itemRepository.findById(itemId).orElse(null);

        ItemBookingDto lastBooking = bookingDtoMapper.toItemBookingDto(
                bookingRepository.findFirstByItemIdAndStartTimeBeforeAndStatusOrderByStartTimeDesc(itemId,
                                LocalDateTime.now(),
                                BookingStatus.APPROVED)
                        .orElse(null));


        ItemBookingDto nextBooking = bookingDtoMapper.toItemBookingDto(
                bookingRepository.findFirstByItemIdAndStartTimeAfterAndStatusOrderByStartTimeAsc(itemId,
                                LocalDateTime.now(),
                                BookingStatus.APPROVED)
                        .orElse(null));

        if (item != null) {
            return itemDtoMapper.toItemWithBookingsDto(item, lastBooking, nextBooking);
        } else throw new NotFoundException("Item not found");
    }

    @Override
    public List<ItemDto> findAll(long userId) {
        userService.findById(userId); // throws 404 if user not found

        return itemRepository.findAllByOwnerId(userId).stream()
                .map(itemDtoMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public ItemDto add(long userId, ItemDto itemDto) {

        if (this.isValidItem(itemDto)) {
            UserDto owner = userService.findById(userId); // throws 404 if user not found
            Item newItem = itemRepository.save(itemDtoMapper.toItem(itemDto, userDtoMapper.toUser(owner)));
            itemDto.setId(newItem.getId());
            return itemDto;
        } else throw new ValidationException("Invalid item received");
    }

    @Transactional
    @Override
    public ItemDto update(long userId, long itemId, ItemDto itemDto) {

        userService.findById(userId); // throws 404 if user not found
        Item itemToUpdate = itemRepository.findById(itemId).orElse(null);
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
        return itemRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndIsAvailable(request, request, true).stream()
                .map(itemDtoMapper::toItemDto)
                .sorted(Comparator.comparing(ItemDto::getId))
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public void delete(long userId, long itemId) {
        userService.findById(userId); // throws 404 if user not found
        Item itemToDelete = itemRepository.findById(itemId).orElse(null);
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
