package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.client.ItemRequestClient;
import ru.practicum.shareit.request.dto.ItemRequestDto;

@Slf4j
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {


    private final ItemRequestClient itemRequestClient;


    // POST /requests
    @PostMapping
    public ResponseEntity<Object> createItemRequest(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
                                                            @RequestBody ItemRequestDto itemRequestDto) {
        log.debug("Received POST-request at /requests endpoint with userId={}, itemRequestDto={}", userId, itemRequestDto);
        return itemRequestClient.createItemRequest(userId, itemRequestDto);
    }

    // GET /requests
    @GetMapping
    public ResponseEntity<Object> getUserItemRequests(@RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        log.debug("Received GET-request at /requests endpoint with userId={}", userId);
        return itemRequestClient.getUserItemRequests(userId);
    }

    // GET /requests/all?from={from}&size={size}
    @GetMapping("/all")
    public ResponseEntity<Object> getOtherUsersItemRequests(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
                                                                          @RequestParam(defaultValue = "0") Integer from,
                                                                          @RequestParam(defaultValue = "100") Integer size) {
        log.debug("Received GET-request at /requests/all endpoint with from={}, size={}", from, size);
        return itemRequestClient.getOtherUsersItemRequests(userId, from, size);
    }

    // GET /requests/{requestId}
    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getItemRequestById(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
                                                             @PathVariable Long requestId) {
        log.debug("Received GET-request at /requests/ endpoint with requestId={}", requestId);
        return itemRequestClient.getItemRequestById(userId, requestId);
    }
}
