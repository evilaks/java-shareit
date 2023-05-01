package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDtoMapper;
import ru.practicum.shareit.booking.dto.ItemBookingDto;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.CommentRepository;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoMapper;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.util.exception.BadRequestException;
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
    private final CommentRepository commentRepository;
    private final CommentDtoMapper commentDtoMapper;
    private final ItemRequestRepository itemRequestRepository;

    @Override
    public ItemWithBookingsDto findById(long itemId, long userId) {

        // throws 404 if user not found
        userService.findById(userId);

        Item item = itemRepository.findById(itemId).orElse(null);

        if (item != null) {
            if (item.getOwner().getId().equals(userId)) {
                return this.addBookingsAndCommentsToItem(item);
            } else {
                List<CommentDto> comments = commentRepository.findAllByItemIdOrderByCreatedDesc(item.getId()).stream()
                        .map(commentDtoMapper::toDto)
                        .collect(Collectors.toList());
                return itemDtoMapper.toItemWithBookingsDto(item, null, null, comments);
            }
        } else throw new NotFoundException("Item not found");
    }

    @Override
    public List<ItemWithBookingsDto> findAll(long userId, Integer from, Integer size) {

        userService.findById(userId); // throws 404 if user not found

        // validate from and size
        if (from < 0 || size < 1) {
            throw new ValidationException("Invalid from or size");
        }

        // convert from to page
        int page = from > 0 ? from / size : 0;

        return itemRepository.findAllByOwnerIdOrderById(userId, PageRequest.of(page, size)).stream()
                .map(this::addBookingsAndCommentsToItem)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public ItemDto add(long userId, ItemDto itemDto) {

        if (this.isValidItem(itemDto)) {
            UserDto owner = userService.findById(userId); // throws 404 if user not found

            ItemRequest itemRequest = null;
            if (itemDto.getRequestId() != null) {
                itemRequest = itemRequestRepository.findById(itemDto.getRequestId()).orElse(null);
            }

            Item newItem = itemRepository.save(itemDtoMapper.toItem(itemDto, userDtoMapper.toUser(owner), itemRequest));
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
    public List<ItemDto> search(long userId, String request, Integer from, Integer size) {
        userService.findById(userId); // throws 404 if user not found

        // validate from and size
        if (from < 0 || size < 1) {
            throw new ValidationException("Invalid from or size");
        }

        // convert from to page
        int page = from > 0 ? from / size : 0;

        if (request.isBlank()) return new ArrayList<>();

        return itemRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndIsAvailable(request,
                        request, true, PageRequest.of(page, size)).stream()
                .map(itemDtoMapper::toItemDto)
                .sorted(Comparator.comparing(ItemDto::getId))
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public CommentDto addComment(long userId, long itemId, Comment comment) {
        // throws 404 if user not found
        UserDto userDto = userService.findById(userId);

        if (comment.getText() == null || comment.getText().isBlank()) throw new BadRequestException("Comment text is empty");

        Item item = itemRepository.findById(itemId).orElse(null);

        // check if item has previous bookings from this user and save comment
        if (item != null) {
            if (bookingRepository.existsByItemIdAndBookerIdAndStatusAndEndTimeBefore(itemId,
                    userId,
                    BookingStatus.APPROVED,
                    LocalDateTime.now())) {
                comment.setItem(item);
                comment.setAuthor(userDtoMapper.toUser(userDto));
                comment.setCreated(LocalDateTime.now());
                Comment newComment = commentRepository.save(comment);
                return commentDtoMapper.toDto(newComment);
            } else throw new BadRequestException("User has no previous bookings for this item");
        } else throw new NotFoundException("Item not found");

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

    private ItemWithBookingsDto addBookingsAndCommentsToItem(Item item) {

        List<CommentDto> comments = commentRepository.findAllByItemIdOrderByCreatedDesc(item.getId()).stream()
                .map(commentDtoMapper::toDto)
                .collect(Collectors.toList());

        ItemBookingDto lastBooking = bookingDtoMapper.toItemBookingDto(
                    bookingRepository.findFirstByItemIdAndStartTimeBeforeAndStatusOrderByStartTimeDesc(item.getId(),
                            LocalDateTime.now(),
                            BookingStatus.APPROVED)
                            .orElse(null));

        ItemBookingDto nextBooking = bookingDtoMapper.toItemBookingDto(
                    bookingRepository.findFirstByItemIdAndStartTimeAfterAndStatusOrderByStartTimeAsc(item.getId(),
                            LocalDateTime.now(),
                            BookingStatus.APPROVED)
                            .orElse(null));

        return itemDtoMapper.toItemWithBookingsDto(item, lastBooking, nextBooking, comments);
    }

}
