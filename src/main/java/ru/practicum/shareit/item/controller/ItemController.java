package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @PostMapping()
    public ResponseEntity<ItemDto> addItem(@RequestHeader(value = "X-Sharer-User-Id") Long ownerId,
                                           @RequestBody ItemDto itemDto) {
        log.debug("Received POST request to /items endpoint with userId={} and {}", ownerId, itemDto);
        return ResponseEntity.ok().body(itemService.add(ownerId, itemDto));
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemDto> updateItem(@RequestHeader(value = "X-Sharer-User-Id") Long ownerId,
                                              @RequestBody ItemDto itemDto,
                                              @PathVariable Long itemId) {
        log.debug("Received PATCH request to /items/{} endpoint with userId={} and {}", itemId, ownerId, itemDto);
        return ResponseEntity.ok().body(itemService.update(ownerId, itemId, itemDto));
    }

    @GetMapping
    public ResponseEntity<List<ItemWithBookingsDto>> findAllItems(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
                                                                  @RequestParam(defaultValue = "0") Integer from,
                                                                  @RequestParam(defaultValue = "100") Integer size) {
        log.debug("Received GET request to /items endpoint with userId={}", userId);
        return ResponseEntity.ok().body(itemService.findAll(userId, from, size));
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemWithBookingsDto> findItemById(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
                                                            @PathVariable Long itemId) {
        log.debug("Received GET request to /items/{} endpoint with userId={}", itemId, userId);
        return ResponseEntity.ok().body(itemService.findById(itemId, userId));
    }

    @GetMapping("/search")
    public ResponseEntity<List<ItemDto>> searchItems(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
                                                     @RequestParam String text,
                                                     @RequestParam(defaultValue = "0") Integer from,
                                                     @RequestParam(defaultValue = "100") Integer size) {
        log.debug("Received GET request to /search endpoint with userId={} and text-param={}", userId, text);

        return ResponseEntity.ok().body(itemService.search(userId, text, from, size));
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Void> deleteItemById(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
                               @PathVariable Long itemId) {
        log.debug("Received DELETE request to /items/{} endpoint with userId={}", itemId, userId);
        itemService.delete(userId, itemId);
        return ResponseEntity.ok().build();
    }

    // POST /items/{itemId}/comment
    @PostMapping("/{itemId}/comment")
    public ResponseEntity<CommentDto> addComment(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
                                                 @PathVariable Long itemId,
                                                 @RequestBody Comment comment) {
        log.debug("Received POST request to /items/{}/comment endpoint with userId={} and comment={}", itemId, userId, comment);
        return ResponseEntity.ok().body(itemService.addComment(userId, itemId, comment));
    }

}