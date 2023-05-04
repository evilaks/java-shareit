package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {


    private final ItemRequestService itemRequestService;


    // POST /requests
    @PostMapping
    public ResponseEntity<ItemRequestDto> createItemRequest(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
                                                            @RequestBody ItemRequestDto itemRequestDto) {
        log.debug("Received POST-request at /requests endpoint with userId={}, itemRequestDto={}", userId, itemRequestDto);
        return ResponseEntity.ok().body(itemRequestService.createItemRequest(userId, itemRequestDto));
    }

    // GET /requests
    @GetMapping
    public ResponseEntity<List<ItemRequestDto>> getUserItemRequests(@RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        log.debug("Received GET-request at /requests endpoint with userId={}", userId);
        return ResponseEntity.ok().body(itemRequestService.getUserItemRequests(userId));
    }

    // GET /requests/all?from={from}&size={size}
    @GetMapping("/all")
    public ResponseEntity<List<ItemRequestDto>> getOtherUsersItemRequests(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
                                                                          @RequestParam(defaultValue = "0") Integer from,
                                                                          @RequestParam(defaultValue = "100") Integer size) {
        log.debug("Received GET-request at /requests/all endpoint with from={}, size={}", from, size);
        return ResponseEntity.ok().body(itemRequestService.getOtherUsersItemRequests(userId, from, size));
    }

    // GET /requests/{requestId}
    @GetMapping("/{requestId}")
    public ResponseEntity<ItemRequestDto> getItemRequestById(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
                                                             @PathVariable Long requestId) {
        log.debug("Received GET-request at /requests/ endpoint with requestId={}", requestId);
        return ResponseEntity.ok().body(itemRequestService.getItemRequestById(userId, requestId));
    }
}
