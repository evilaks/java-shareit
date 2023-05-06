package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.client.ItemClient;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.util.exception.ValidationException;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;


@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemClient itemClient;

    @PostMapping()
    public ResponseEntity<Object> addItem(@Positive @RequestHeader(value = "X-Sharer-User-Id") Long ownerId,
                                          @RequestBody ItemDto itemDto) {
        log.debug("Received POST request to /items endpoint with userId={} and {}", ownerId, itemDto);
        if (!isValidItem(itemDto)) {
            throw new ValidationException("Invalid item: " + itemDto);
        }
        return itemClient.add(ownerId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@Positive @RequestHeader(value = "X-Sharer-User-Id") Long ownerId,
                                             @RequestBody ItemDto itemDto,
                                             @Positive @PathVariable Long itemId) {
        log.debug("Received PATCH request to /items/{} endpoint with userId={} and {}", itemId, ownerId, itemDto);
        return itemClient.update(ownerId, itemId, itemDto);
    }

    @GetMapping
    public ResponseEntity<Object> findAllItems(@Positive @RequestHeader(value = "X-Sharer-User-Id") Long userId,
                                               @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                               @Positive @RequestParam(defaultValue = "100") Integer size) {
        log.debug("Received GET request to /items endpoint with userId={}", userId);
        return itemClient.findAll(userId, from, size);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> findItemById(@Positive @RequestHeader(value = "X-Sharer-User-Id") Long userId,
                                               @Positive @PathVariable Long itemId) {
        log.debug("Received GET request to /items/{} endpoint with userId={}", itemId, userId);
        return itemClient.findById(itemId, userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(@Positive @RequestHeader(value = "X-Sharer-User-Id") Long userId,
                                              @RequestParam String text,
                                              @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                              @Positive @RequestParam(defaultValue = "100") Integer size) {
        log.debug("Received GET request to /search endpoint with userId={} and text-param={}", userId, text);
        return itemClient.search(userId, text, from, size);
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Void> deleteItemById(@Positive @RequestHeader(value = "X-Sharer-User-Id") Long userId,
                                               @Positive @PathVariable Long itemId) {
        log.debug("Received DELETE request to /items/{} endpoint with userId={}", itemId, userId);
        itemClient.delete(userId, itemId);
        return ResponseEntity.ok().build();
    }

    // POST /items/{itemId}/comment
    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@Positive @RequestHeader(value = "X-Sharer-User-Id") Long userId,
                                             @Positive @PathVariable Long itemId,
                                             @RequestBody CommentDto comment) {
        log.debug("Received POST request to /items/{}/comment endpoint with userId={} and comment={}", itemId, userId, comment);
        return itemClient.addComment(userId, itemId, comment);
    }

    private boolean isValidItem(ItemDto itemDto) {
        if (itemDto == null) {
            return false;
        }

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